package com.eldercare.system.migration;

import com.eldercare.system.util.AesUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.Map;

/**
 * 历史身份证数据加密迁移（一次性任务）。
 *
 * <p>把 customer 表里已是明文的 id_number 批量加密，并回填 id_number_hash。
 * 通过启动参数 --migrate.id-number=true 触发，默认不执行，避免每次启动都跑。
 *
 * <p>幂等设计：通过尝试解密判断当前值是密文还是明文——
 * 能解密说明已是密文（同时补齐可能缺失的 hash）；不能解密说明是明文，加密并补 hash。
 * 避免重复加密，也能修复「已加密但 hash 为空」的中间状态。
 *
 * <p>运行方式：在 IDE 运行配置或命令行加参数：
 * <pre>
 *   java -jar app.jar --migrate.id-number=true
 * </pre>
 */
@Component
public class IdNumberEncryptionMigration implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    public IdNumberEncryptionMigration(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        // 只有显式传参才执行，避免每次启动都跑迁移
        boolean shouldRun = false;
        for (String arg : args) {
            if ("--migrate.id-number=true".equals(arg)) {
                shouldRun = true;
                break;
            }
        }
        if (!shouldRun) {
            return;
        }

        System.out.println("====== 开始身份证数据加密迁移 ======");

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT customer_id, id_number, id_number_hash FROM customer WHERE del_flag = '0'"
        );

        int migrated = 0;
        int skipped = 0;
        for (Map<String, Object> row : rows) {
            Long customerId = ((Number) row.get("customer_id")).longValue();
            String currentId = (String) row.get("id_number");
            if (currentId == null || currentId.isEmpty()) {
                continue;
            }

            // 判断当前值是否已是密文：尝试解密，能解出来说明已是密文
            // 不能解密（抛异常）说明是明文，需要加密
            String plain;
            try {
                plain = AesUtil.decrypt(currentId);
            } catch (Exception e) {
                // 解密失败 → 是明文
                plain = currentId;
            }

            String encrypted = AesUtil.encrypt(plain);
            String hash = sha256Hex(plain);

            // 幂等：若当前 id_number 已是密文且 hash 已存在，跳过，避免重复 UPDATE
            if (plain.equals(currentId) == false
                    && row.get("id_number_hash") != null
                    && !"".equals(((String) row.getOrDefault("id_number_hash", "")).trim())
                    && encrypted.equals(currentId)) {
                skipped++;
                continue;
            }

            jdbcTemplate.update(
                    "UPDATE customer SET id_number = ?, id_number_hash = ? WHERE customer_id = ?",
                    encrypted, hash, customerId
            );
            migrated++;
        }

        System.out.println("====== 迁移完成：处理 " + migrated + " 条，跳过(已就绪) " + skipped + " 条 ======");
        System.out.println("====== 提示：迁移完成后可删除 --migrate.id-number=true 参数 ======");
    }

    /**
     * SHA-256 哈希，与 CustomerServiceImpl.hashIdNumber 算法完全一致。
     */
    private String sha256Hex(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 计算失败", e);
        }
    }
}
