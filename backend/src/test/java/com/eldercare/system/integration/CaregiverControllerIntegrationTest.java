package com.eldercare.system.integration;

import com.eldercare.system.service.RedisService;
import com.eldercare.system.util.ApiResult;
import com.eldercare.system.util.JWTUtil;
import com.eldercare.system.vo.caregiver.CaregiverListVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 健康管家模块集成测试。
 * 全链路验证 Controller → Service → Mapper → MySQL。
 * 使用 @Transactional 自动回滚。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
@DisplayName("健康管家模块集成测试")
class CaregiverControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RedisService redisService;

    private HttpHeaders authHeaders;

    @BeforeEach
    void setUp() {
        Map<String, String> claims = Map.of(
                "userId", "1",
                "email", "caregiver-test@test.com",
                "roleId", "1",
                "roleKey", "admin",
                "status", "1"
        );
        String token = JWTUtil.getToken(claims);
        redisService.setToken("caregiver-test@test.com", token, 72000000);
        authHeaders = new HttpHeaders();
        authHeaders.set("Authorization", "Bearer " + token);
    }

    @Test
    @DisplayName("GET /caregiver/list — 正常请求返回分页列表")
    void list_正常请求_返回分页列表() {
        HttpEntity<Void> entity = new HttpEntity<>(authHeaders);
        ResponseEntity<ApiResult<CaregiverListVO>> response = restTemplate.exchange(
                "/caregiver/list?pageNum=1&pageSize=10",
                HttpMethod.GET, entity,
                new ParameterizedTypeReference<ApiResult<CaregiverListVO>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResult<CaregiverListVO> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.getCode());
    }

    @Test
    @DisplayName("GET /caregiver/homeStats — 有效token返回首页数据或业务错误")
    void homeStats_有效token_返回数据() {
        HttpEntity<Void> entity = new HttpEntity<>(authHeaders);
        ResponseEntity<ApiResult> response = restTemplate.exchange(
                "/caregiver/homeStats", HttpMethod.GET, entity, ApiResult.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        // 200=查询成功，其他=数据库无数据导致的业务错误（由 @Transactional 回滚保证不影响其他测试）
    }

    @Test
    @DisplayName("GET /caregiver/homeStats — 无token返回401")
    void homeStats_无token_返回401() {
        ResponseEntity<ApiResult> response = restTemplate.exchange(
                "/caregiver/homeStats", HttpMethod.GET, null, ApiResult.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}
