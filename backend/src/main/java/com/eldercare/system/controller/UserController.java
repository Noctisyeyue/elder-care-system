package com.eldercare.system.controller;

import com.eldercare.system.entity.User;
import com.eldercare.system.util.PasswordUtil;
import com.eldercare.system.util.ImgUploadUtil;
import com.eldercare.system.dto.user.*;
import com.eldercare.system.vo.user.*;
import com.eldercare.system.util.ApiResult;
import com.eldercare.system.vo.user.RoleNumVO;
import com.eldercare.system.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/** 用户管理控制器 */
@Tag(name = "用户管理接口")
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    // 登录
    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public ApiResult<LoginResponseVO> login(@RequestBody UserLoginRequest user) {
        return userService.login(user);
    }
    // 查询用户
    @Operation(summary = "查询用户列表")
    @GetMapping("/list")
    public ApiResult<UserListResultVO> list( UserListRequest user) {
        return userService.list(user);
    }
    // 增加用户
    @Operation(summary = "增加用户")
    @PostMapping("/add")
    public ApiResult add(@RequestBody UserAddRequest user) {
        return userService.add(user);
    }
    // 护工注册
    @Operation(summary = "护工注册")
    @PostMapping("/register")
    public ApiResult register(@RequestBody CaregiverRegisterRequest request) {
        return userService.register(request);
    }
    // 删除用户
    @Operation(summary = "删除用户")
    @PostMapping("/del")
    public ApiResult delete(@RequestBody Map<String, List<String>> payload) {
        List<String> userNames = payload.get("userNames");
        return userService.delete(userNames);
    }
    // 修改用户
    @Operation(summary = "修改用户信息")
    @PostMapping("/update")
    public ApiResult update(@RequestBody UserAddRequest user) {
        return userService.update(user);
    }

    // 获取用户总数
    @Operation(summary = "获取用户总数")
    @GetMapping("/count")
    public ApiResult<Long> count() {
        return userService.count();
    }

    @Operation(summary = "获取各角色用户数量")
    @GetMapping("/roleNum")
    public ApiResult<List<RoleNumVO>> roleNum() {
        return userService.roleNum();
    }
    //上传头像
    @Operation(summary = "上传用户头像")
    @PostMapping("/avatar/upload")
    public ApiResult<String> uploadAvatar(@RequestParam("file") MultipartFile file,@RequestHeader(value = "Authorization", required = false) String token) {
        return userService.uploadFile(file,token);
    }
    //获取用户头像
    @Operation(summary = "获取用户头像")
    @GetMapping("/avatar/get")
    public ApiResult<String> getAvatar(@RequestHeader(value = "Authorization", required = false) String token) {
        return userService.getAvatar(token);
    }
    //获取用户邮箱
    @Operation(summary = "获取用户邮箱")
    @GetMapping("/email/get")
    public ApiResult<String> getEmail(@RequestHeader(value = "Authorization", required = false) String token) {
        return userService.getEmail(token);
    }
}
