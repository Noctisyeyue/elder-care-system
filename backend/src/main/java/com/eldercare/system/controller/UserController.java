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
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("/list")
    public ApiResult<UserListResultVO> list( UserListRequest user) {
        return userService.list(user);
    }
    // 增加用户
    @Operation(summary = "增加用户")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
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
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping("/del")
    public ApiResult delete(@RequestBody Map<String, List<String>> payload) {
        List<String> userNames = payload.get("userNames");
        return userService.delete(userNames);
    }
    // 修改用户
    @Operation(summary = "修改用户信息")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping("/update")
    public ApiResult update(@RequestBody UserAddRequest user) {
        return userService.update(user);
    }
    // 审核护工（仅超级管理员）
    @Operation(summary = "审核护工")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping("/audit")
    public ApiResult audit(@RequestParam Long userId) {
        return userService.audit(userId);
    }
    // 启用账号（仅超级管理员，将 status 从 2 改回 1）
    @Operation(summary = "启用账号")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping("/enable")
    public ApiResult enable(@RequestParam Long userId) {
        return userService.enable(userId);
    }
    // 禁用账号（仅超级管理员，不可禁用超管或自己）
    @Operation(summary = "禁用账号")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping("/disable")
    public ApiResult disable(@RequestParam Long userId) {
        return userService.disable(userId);
    }
    // 重置密码为默认密码（仅超级管理员）
    @Operation(summary = "重置密码")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping("/resetPwd")
    public ApiResult resetPassword(@RequestParam Long userId) {
        return userService.resetPassword(userId);
    }

    // 获取用户总数
    @Operation(summary = "获取用户总数")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @GetMapping("/count")
    public ApiResult<Long> count() {
        return userService.count();
    }

    @Operation(summary = "获取各角色用户数量")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @GetMapping("/roleNum")
    public ApiResult<List<RoleNumVO>> roleNum() {
        return userService.roleNum();
    }
    //上传头像
    @Operation(summary = "上传用户头像")
    @PostMapping("/avatar/upload")
    public ApiResult<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        return userService.uploadFile(file);
    }
    //获取用户头像
    @Operation(summary = "获取用户头像")
    @GetMapping("/avatar/get")
    public ApiResult<String> getAvatar() {
        return userService.getAvatar();
    }
    //获取用户邮箱
    @Operation(summary = "获取用户邮箱")
    @GetMapping("/email/get")
    public ApiResult<String> getEmail() {
        return userService.getEmail();
    }
    //获取当前用户最新状态（待审核护工刷新用）
    @Operation(summary = "获取当前用户状态")
    @GetMapping("/status/get")
    public ApiResult<Integer> getStatus() {
        return userService.getStatus();
    }

    @Operation(summary = "获取当前用户资料")
    @GetMapping("/profile/get")
    public ApiResult<UserProfileVO> getProfile() {
        return userService.getProfile();
    }

    @Operation(summary = "更新当前用户资料")
    @PostMapping("/profile/update")
    public ApiResult updateProfile(@RequestBody UserProfileUpdateRequest request) {
        return userService.updateProfile(request);
    }

    @Operation(summary = "当前用户修改密码")
    @PostMapping("/password/update")
    public ApiResult changePassword(@RequestBody UserPasswordUpdateRequest request) {
        return userService.changePassword(request);
    }

    @Operation(summary = "发送邮箱修改验证码")
    @PostMapping("/email/change/code")
    public ApiResult sendEmailChangeCode(@RequestBody EmailChangeCodeRequest request) {
        return userService.sendEmailChangeCode(request);
    }

    @Operation(summary = "确认修改邮箱")
    @PostMapping("/email/change")
    public ApiResult changeEmail(@RequestBody EmailChangeRequest request) {
        return userService.changeEmail(request);
    }
}
