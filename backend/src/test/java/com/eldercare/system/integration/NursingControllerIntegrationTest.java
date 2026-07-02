package com.eldercare.system.integration;

import com.eldercare.system.service.RedisService;
import com.eldercare.system.util.ApiResult;
import com.eldercare.system.util.JWTUtil;
import com.eldercare.system.vo.nursing.ItemListVO;
import com.eldercare.system.vo.nursing.LevelListVO;
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
 * 护理管理模块集成测试。
 * 全链路验证 Controller → Service → Mapper → MySQL。
 * 使用 @Transactional 自动回滚。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
@DisplayName("护理管理模块集成测试")
class NursingControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RedisService redisService;

    private HttpHeaders authHeaders;

    @BeforeEach
    void setUp() {
        Map<String, String> claims = Map.of(
                "userId", "1",
                "email", "nursing-test@test.com",
                "roleId", "1",
                "roleKey", "admin",
                "status", "1"
        );
        String token = JWTUtil.getToken(claims);
        redisService.setToken("nursing-test@test.com", token, 72000000);
        authHeaders = new HttpHeaders();
        authHeaders.set("Authorization", "Bearer " + token);
    }

    @Test
    @DisplayName("GET /nursing/level/list — 返回级别列表")
    void levelList_正常请求_返回级别列表() {
        HttpEntity<Void> entity = new HttpEntity<>(authHeaders);
        ResponseEntity<ApiResult<LevelListVO>> response = restTemplate.exchange(
                "/nursing/level/list?status=启用&pageNum=1&pageSize=10",
                HttpMethod.GET, entity,
                new ParameterizedTypeReference<ApiResult<LevelListVO>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResult<LevelListVO> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.getCode());
    }

    @Test
    @DisplayName("GET /nursing/item/list — 返回项目列表")
    void itemList_正常请求_返回项目列表() {
        HttpEntity<Void> entity = new HttpEntity<>(authHeaders);
        ResponseEntity<ApiResult<ItemListVO>> response = restTemplate.exchange(
                "/nursing/item/list?status=启用&pageNum=1&pageSize=10",
                HttpMethod.GET, entity,
                new ParameterizedTypeReference<ApiResult<ItemListVO>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResult<ItemListVO> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.getCode());
    }

    @Test
    @DisplayName("GET /nursing/level/list — 无token返回401")
    void levelList_无token_返回401() {
        ResponseEntity<ApiResult> response = restTemplate.exchange(
                "/nursing/level/list?status=启用&pageNum=1&pageSize=10",
                HttpMethod.GET, null, ApiResult.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @DisplayName("DELETE /nursing/item/delete/{id} — 测试权限校验通过")
    void deleteItem_有token_正常执行() {
        HttpEntity<Void> entity = new HttpEntity<>(authHeaders);
        ResponseEntity<ApiResult> response = restTemplate.exchange(
                "/nursing/item/delete/999999", HttpMethod.DELETE, entity, ApiResult.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResult body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.getCode());
    }
}
