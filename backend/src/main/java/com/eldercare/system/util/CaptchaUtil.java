package com.eldercare.system.util;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 算术验证码图片生成工具，不访问 Redis、不读取配置，只负责生成题目、答案和图片。
 */
public class CaptchaUtil {

    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * 生成结果：正确答案 + 完整 data URI。
     */
    @Data
    @AllArgsConstructor
    public static class CaptchaGenerateResult {
        /** 正确答案，例如 "8" */
        private String answer;
        /** 完整 data URI，可直接给前端 img.src 使用 */
        private String imageDataUri;
    }

    /**
     * 生成算术验证码图片。
     *
     * @param width  图片宽度
     * @param height 图片高度
     * @return 包含答案和 data URI 的生成结果
     */
    public static CaptchaGenerateResult generate(int width, int height) {
        // 1. 随机生成两个 0~9 的整数
        int a = RANDOM.nextInt(10);
        int b = RANDOM.nextInt(10);

        // 2. 计算答案
        String answer = String.valueOf(a + b);

        // 3. 表达式文本
        String expression = a + " + " + b + " = ?";

        // 4. 创建 BufferedImage
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // 5. 绘制浅色背景
        g.setColor(new Color(240, 242, 245));
        g.fillRect(0, 0, width, height);

        // 6. 设置抗锯齿
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // 7. 绘制干扰线（2 条）
        g.setColor(new Color(180, 188, 200));
        for (int i = 0; i < 2; i++) {
            int x1 = RANDOM.nextInt(width / 3);
            int y1 = RANDOM.nextInt(height);
            int x2 = width - RANDOM.nextInt(width / 3);
            int y2 = RANDOM.nextInt(height);
            g.drawLine(x1, y1, x2, y2);
        }

        // 8. 绘制表达式文本
        g.setColor(new Color(44, 62, 80));
        Font font = new Font("Arial", Font.BOLD, 22);
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(expression);
        int textHeight = fm.getHeight();
        int x = (width - textWidth) / 2;
        int y = (height - textHeight) / 2 + fm.getAscent();
        g.drawString(expression, x, y);

        g.dispose();

        // 9. 编码为 PNG → Base64 → data URI
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", baos);
        } catch (IOException e) {
            throw new RuntimeException("验证码图片生成失败", e);
        }
        String base64 = Base64.getEncoder().encodeToString(baos.toByteArray());
        String imageDataUri = "data:image/png;base64," + base64;

        return new CaptchaGenerateResult(answer, imageDataUri);
    }
}
