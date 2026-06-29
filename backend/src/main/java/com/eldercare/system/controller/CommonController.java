package com.eldercare.system.controller;

import com.eldercare.system.email.LoginParam;
import com.eldercare.system.email.R;
import com.eldercare.system.util.ApiResult;
import com.eldercare.system.vo.common.NotificationVO;
import com.eldercare.system.service.CommonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/** 邮箱验证控制器 */
@Tag(name = "邮箱验证接口")
@RestController
@RequestMapping("/common")
@CrossOrigin
public class CommonController {

    @Autowired
    private CommonService commonService;

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

    @Operation(summary = "获取通知信息")
    @GetMapping("/notification")
    public ApiResult<NotificationVO> getNotification(String date){
        return commonService.getNotification(date);
    }

}
