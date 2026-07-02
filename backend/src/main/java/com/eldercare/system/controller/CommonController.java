package com.eldercare.system.controller;

import com.eldercare.system.config.CaptchaProperties;
import com.eldercare.system.email.LoginParam;
import com.eldercare.system.email.R;
import com.eldercare.system.email.RedisConstant;
import com.eldercare.system.util.ApiResult;
import com.eldercare.system.util.CaptchaUtil;
import com.eldercare.system.vo.common.CaptchaVO;
import com.eldercare.system.vo.common.NotificationVO;
import com.eldercare.system.service.CommonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/** 通用控制器（邮箱验证、验证码等） */
@Tag(name = "通用接口")
@RestController
@RequestMapping("/common")
@CrossOrigin
public class CommonController {

    @Autowired
    private CommonService commonService;

    @Autowired
    private CaptchaProperties captchaProperties;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    // 权限码请求接口
    @Operation(summary = "权限码请求接口")
    @PostMapping("/code/request")
    public R getRequestPermissionCode(@RequestBody String emailJson) {
        return commonService.getRequestPermissionCode(emailJson);
    }

    // 邮箱验证码接口
    @Operation(summary = "邮箱验证码接口")
    @PostMapping("/code/email")
    public R sendEmailCode(@RequestBody LoginParam loginParam) {
        return commonService.sendEmailCode(loginParam);
    }

    //重置密码接口
    @Operation(summary = "重置密码接口")
    @PostMapping("/code/findPassword")
    public R findPassword(@RequestBody LoginParam loginParam) {
        return commonService.findPassword(loginParam);
    }

    // 护工注册验证码接口
    @Operation(summary = "护工注册验证码接口")
    @PostMapping("/code/register")
    public R sendRegisterCode(@RequestBody String emailJson) {
        return commonService.sendRegisterCode(emailJson);
    }

    @Operation(summary = "获取通知信息")
    @GetMapping("/notification")
    public ApiResult<NotificationVO> getNotification(String date){
        return commonService.getNotification(date);
    }

    @Operation(summary = "获取登录验证码")
    @GetMapping("/captcha")
    public ApiResult<CaptchaVO> getCaptcha() {
        ApiResult<CaptchaVO> result = new ApiResult<>();
        CaptchaVO captchaVO = new CaptchaVO();

        if (!Boolean.TRUE.equals(captchaProperties.getEnabled())) {
            captchaVO.setCaptchaEnabled(false);
            result.setCode(200);
            result.setMessage("获取成功");
            result.setData(captchaVO);
            return result;
        }

        // 生成唯一标识和验证码图片
        String uuid = UUID.randomUUID().toString().replace("-", "");
        CaptchaUtil.CaptchaGenerateResult captchaResult = CaptchaUtil.generate(
                captchaProperties.getWidth(), captchaProperties.getHeight());

        // 存入 Redis，过期时间由配置决定
        String redisKey = RedisConstant.LOGIN_CAPTCHA + uuid;
        stringRedisTemplate.opsForValue().set(
                redisKey, captchaResult.getAnswer(),
                captchaProperties.getExpireSeconds(), TimeUnit.SECONDS);

        captchaVO.setCaptchaEnabled(true);
        captchaVO.setUuid(uuid);
        captchaVO.setImg(captchaResult.getImageDataUri());
        result.setCode(200);
        result.setMessage("获取成功");
        result.setData(captchaVO);
        return result;
    }

}
