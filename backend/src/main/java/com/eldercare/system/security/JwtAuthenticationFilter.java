package com.eldercare.system.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.eldercare.system.service.RedisService;
import com.eldercare.system.util.ApiResult;
import com.eldercare.system.util.JWTUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * JWT 认证过滤器：解析 Bearer token、校验 Redis 最新 token、校验账号状态，
 * 通过后构造 Authentication 写入 SecurityContextHolder。
 *
 * <p>逻辑等价于旧 JWTInterceptor.preHandle()，迁移后由 SecurityFilterChain 统一调度。
 * 不加 @Component，由 SecurityConfig 手动 new 并注册，避免被 Servlet 容器重复注册。
 */
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /** 待审核账号（status=0）允许访问的接口白名单 */
    private static final Set<String> PENDING_REVIEW_WHITELIST = Set.of(
            "/user/status/get"
    );

    /** Redis 服务，用于读取/清除服务端保存的登录令牌 */
    private final RedisService redisService;

    /**
     * 创建 JWT 认证过滤器
     *
     * @param redisService Redis 服务
     */
    public JwtAuthenticationFilter(RedisService redisService) {
        this.redisService = redisService;
    }

    /**
     * 处理每个 HTTP 请求的核心方法。
     *
     * <p>整体流程（按顺序执行，任何一步不通过就放行到后续过滤器处理）：
     * <ol>
     *   <li>从请求头取 Authorization，去掉 "Bearer " 前缀拿到 token</li>
     *   <li>无 token → 放行（后续 ExceptionTranslationFilter 触发 401）</li>
     *   <li>用密钥解析 JWT，验签（防止伪造 token）</li>
     *   <li>对比 Redis 中的 token，确保是最新签发的（旧 token 不认）</li>
     *   <li>检查账号状态：禁用(status=2)返回403，待审核(status=0)仅白名单</li>
     *   <li>全部通过 → 造一个通行令牌存到 SecurityContextHolder，后续代码知道"当前谁在发请求"</li>
     * </ol>
     *
     * <p>重要区别：
     * <ul>
     *   <li>token缺失/过期/签名错误 → 不写入SecurityContext，放行，由后续过滤器统一返回401</li>
     *   <li>账号禁用/待审核越权 → 直接在response里写JSON返回403，结束请求</li>
     * </ul>
     *
     * @param request     HTTP 请求（可以从中取 Header、URL 等信息）
     * @param response    HTTP 响应（可以直接往里面写 JSON 终止请求）
     * @param filterChain 过滤器链（调用 doFilter 表示"放行，交给下一个过滤器"）
     * @throws ServletException 过滤器链异常
     * @throws IOException      读写异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // ────────── 步骤1：从请求头取出 token ──────────
        // 前端在 request.ts 拦截器里写的：config.headers.Authorization = `Bearer ${token}`
        // 格式："Bearer eyJhbGciOi..."，前7个字符是 "Bearer "，要去掉
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);  // 截掉 "Bearer "，只保留后面的 token 部分
        }

        try {
            // ────────── 步骤2：没有 token → 放行，后续过滤器会返回 401 ──────────
            if (token == null) {
                filterChain.doFilter(request, response);  // doFilter = "交给下一个过滤器处理"
                return;  // 注意：doFilter 不会终止方法，必须 return
            }

            // ────────── 步骤3：解析 JWT，验证签名 ──────────
            // JWTUtil.getPayloadFromToken() 内部会用密钥验签，验签失败会抛异常
            // claims 就是 JWT 里存的那些字段：email、roleKey、userId、status 等
            Map<String, Claim> claims = JWTUtil.getPayloadFromToken(token);
            String email = claims.get("email").asString();

            // ────────── 步骤4：Redis 对比，确保 token 是最新签发的 ──────────
            // 场景：用户在A电脑登录 → 拿到token_A → 在B电脑登录 → 拿到token_B
            // 此时 Redis 里存的是 token_B，token_A 就失效了，防止多地登录共用
            String redisToken = redisService.getToken(email);
            if (redisToken == null || !redisToken.equals(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            // ────────── 步骤5a：账号状态校验 ──────────
            // status: 0=待审核  1=正常  2=已禁用
            int status = parseIntClaim(claims.get("status"));
            if (status < 0) {
                // status 字段缺失或格式异常，按非法 token 处理
                filterChain.doFilter(request, response);
                return;
            }

            // ────────── 步骤5b：账号已被禁用 → 直接返回 403，清除 Redis 中 token ──────────
            if (status == 2) {
                redisService.deleteToken(email);  // 禁用了就别留着了
                writeFail(response, 403, "账号已被禁用，请联系管理员");
                return;  // writeFail 已经往 response 写了 JSON 并终止请求
            }

            // ────────── 步骤5c：待审核账号 → 只能访问白名单接口 ──────────
            String path = request.getServletPath();
            if (status == 0 && !PENDING_REVIEW_WHITELIST.contains(path)) {
                writeFail(response, 403, "账号待审核，暂无权限访问");
                return;
            }

            // ────────── 步骤6：全部通过！造通行令牌，存入全局上下文 ──────────
            // ① 从 JWT 取出 roleKey（如 "nurse"），拼成权限标签（如 "ROLE_NURSE"）
            String roleKey = claims.get("roleKey").asString();
            List<SimpleGrantedAuthority> authorities = List.of(
                    new SimpleGrantedAuthority("ROLE_" + roleKey.toUpperCase())
            );
            // ② 造一个 LoginUser 对象（包含 userId、email、角色信息等）
            LoginUser loginUser = new LoginUser(
                    parseLongClaim(claims.get("userId")),
                    email,
                    parseLongClaim(claims.get("roleId")),
                    roleKey,
                    status
            );
            // ③ 造一个 Spring Security 通行令牌：谁（loginUser）+ 什么权限（authorities）
            //    第二个参数 null 表示不需要密码（JWT 已经验证过了）
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(loginUser, null, authorities);
            // ④ 把令牌存到 SecurityContextHolder —— 此后整个请求生命周期都能拿到当前用户信息
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // ⑤ 放行，交给下一个过滤器
            filterChain.doFilter(request, response);

        } catch (JWTVerificationException e) {
            // JWT 签名验证失败（密钥对不上、token被篡改、格式错误等）
            log.info("JWT签名验证失败");
            SecurityContextHolder.clearContext();
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            // 其他解析异常（token 结构损坏、claims 字段缺失等）
            log.info("token无效");
            SecurityContextHolder.clearContext();
            filterChain.doFilter(request, response);
        }
    }

    /**
     * JSON 序列化器，用于把 Java 对象转成 JSON 字符串写入 response。
     * 例如：ApiResult.fail(403, "权限不足") → {"code":403, "message":"权限不足", "data":null}
     */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 直接把 JSON 错误信息写到 HTTP 响应里，然后结束当前请求。
     * 用于"我知道你是谁，但你没资格做这件事"的场景（403），例如账号禁用、待审核越权。
     *
     * <p>注意：token 缺失/过期/签名错误（401）不走这个方法，而是不放行到 SecurityContext，
     * 由后续的 CustomAuthenticationEntryPoint 统一返回 401 JSON。
     *
     * <p>返回示例：{"code":403,"message":"账号已被禁用，请联系管理员","data":null}
     *
     * @param response HTTP 响应对象，调用方传入，我们往里面写JSON
     * @param code     业务错误码（401 或 403）
     * @param message  提示信息，会显示在用户界面上
     * @throws IOException 写入响应失败时抛出
     */
    private void writeFail(HttpServletResponse response, int code, String message) throws IOException {
        ApiResult<Void> result = ApiResult.fail(code, message);
        String json = objectMapper.writeValueAsString(result);
        response.setStatus(code == 401 ? HttpServletResponse.SC_UNAUTHORIZED : HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().println(json);
    }

    /**
     * 安全解析 JWT 中的整型字段。
     * JWT claims 里存的值可能是 null、空字符串、或非数字，这个方法统一兜底。
     * 解析失败返回 -1（视为非法状态，后续不放行）。
     *
     * @param claim JWT 中的某个字段
     * @return 解析后的整数，失败返回 -1
     */
    private int parseIntClaim(Claim claim) {
        if (claim == null || claim.asString() == null) {
            return -1;
        }
        try {
            return Integer.parseInt(claim.asString());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * 安全解析 JWT 中的长整型字段（如 userId、roleId）。
     * 返回 null 表示字段缺失或格式异常，调用方自行判断。
     *
     * @param claim JWT 中的某个字段
     * @return 解析后的 Long，失败返回 null
     */
    private Long parseLongClaim(Claim claim) {
        if (claim == null || claim.asString() == null) {
            return null;
        }
        try {
            return Long.parseLong(claim.asString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
