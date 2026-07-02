package com.eldercare.system.integration;

import com.eldercare.system.service.RedisService;
import com.eldercare.system.util.ApiResult;
import com.eldercare.system.util.JWTUtil;
import com.eldercare.system.vo.bed.BedStatusDistributionVO;
import com.eldercare.system.vo.bed.PairVO;
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

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 床位管理模块集成测试。
 * 全链路验证 Controller → Service → Mapper → MySQL。
 * 使用 @Transactional 自动回滚。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
@DisplayName("床位管理模块集成测试")
class BedControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RedisService redisService;

    private HttpHeaders authHeaders;

    /** 生成有效 JWT token 并存入 Redis */
    @BeforeEach
    void setUp() {
        Map<String, String> claims = Map.of(
                "userId", "1",
                "email", "bed-test@test.com",
                "roleId", "1",
                "roleKey", "admin",
                "status", "1"
        );
        String token = JWTUtil.getToken(claims);
        redisService.setToken("bed-test@test.com", token, 72000000);
        authHeaders = new HttpHeaders();
        authHeaders.set("Authorization", "Bearer " + token);
    }

    @Test
    @DisplayName("GET /bed/statusDistribution — 返回床位分布")
    void statusDistribution_正常请求_返回床位分布() {
        HttpEntity<Void> entity = new HttpEntity<>(authHeaders);
        ResponseEntity<ApiResult<BedStatusDistributionVO>> response = restTemplate.exchange(
                "/bed/statusDistribution", HttpMethod.GET, entity,
                new ParameterizedTypeReference<ApiResult<BedStatusDistributionVO>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResult<BedStatusDistributionVO> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.getCode());
        BedStatusDistributionVO data = body.getData();
        assertNotNull(data);
        assertNotNull(data.getTotal());
    }

    @Test
    @DisplayName("GET /bed/freeBeds/{roomNumber} — 返回空闲床位列表")
    void freeBeds_传入房间号_返回空闲床位列表() {
        HttpEntity<Void> entity = new HttpEntity<>(authHeaders);
        ResponseEntity<ApiResult<List<PairVO>>> response = restTemplate.exchange(
                "/bed/freeBeds/101?building=A", HttpMethod.GET, entity,
                new ParameterizedTypeReference<ApiResult<List<PairVO>>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(200, response.getBody().getCode());
    }

    @Test
    @DisplayName("GET /bed/floorList — 返回楼层列表")
    void floorList_正常请求_返回楼层列表() {
        HttpEntity<Void> entity = new HttpEntity<>(authHeaders);
        ResponseEntity<ApiResult<List<String>>> response = restTemplate.exchange(
                "/bed/floorList?building=A", HttpMethod.GET, entity,
                new ParameterizedTypeReference<ApiResult<List<String>>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(200, response.getBody().getCode());
    }
}
