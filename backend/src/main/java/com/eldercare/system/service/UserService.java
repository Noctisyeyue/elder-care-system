package com.eldercare.system.service;

import com.eldercare.system.util.PasswordUtil;
import com.eldercare.system.util.ImgUploadUtil;
import com.eldercare.system.dto.user.*;
import com.eldercare.system.vo.user.*;
import com.eldercare.system.util.ApiResult;
import com.eldercare.system.vo.user.RoleNumVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/** 用户服务接口 */
public interface UserService {

    /**
     * 用户登录
     *
     * @param user 登录参数
     * @return 登录结果
     */
    ApiResult<LoginResponseVO> login(UserLoginRequest user);

    /**
     * 新增用户
     *
     * @param user 用户新增参数
     * @return 操作结果
     */
    ApiResult add(UserAddRequest user);

    /**
     * 护工注册
     *
     * @param request 注册参数
     * @return 注册结果
     */
    ApiResult register(CaregiverRegisterRequest request);

    /**
     * 审核护工（将 status 从 0 改为 1），仅限护工角色
     *
     * @param userId 用户 ID
     * @return 操作结果
     */
    ApiResult audit(Long userId);

    /**
     * 启用账号（将 status 从 2 改回 1）
     *
     * @param userId 用户 ID
     * @return 操作结果
     */
    ApiResult enable(Long userId);

    /**
     * 禁用账号（将 status 改为 2），超级管理员不可被禁用，不可禁用自己
     *
     * @param userId 用户 ID
     * @return 操作结果
     */
    ApiResult disable(Long userId);

    /**
     * 重置密码为默认密码（123456）
     *
     * @param userId 用户 ID
     * @return 操作结果
     */
    ApiResult resetPassword(Long userId);

    /**
     * 分页查询用户列表
     *
     * @param user 查询参数
     * @return 用户列表
     */
    ApiResult<UserListResultVO> list(UserListRequest user);

    /**
     * 批量删除用户
     *
     * @param userName 用户名列表
     * @return 操作结果
     */
    ApiResult delete(List<String> userName);

    /**
     * 修改用户信息
     *
     * @param user 用户修改参数
     * @return 操作结果
     */
    ApiResult update(UserAddRequest user);

    /**
     * 统计用户数量
     *
     * @return 用户数量
     */
    ApiResult<Long> count();

    /**
     * 统计各角色用户数量
     *
     * @return 角色人数统计
     */
    ApiResult<List<RoleNumVO>> roleNum();

    /**
     * 根据邮箱判断用户是否存在
     *
     * @param email 邮箱地址
     * @return true=用户存在，false=用户不存在
     */
    boolean getUserByEmail(String email);

    /**
     * 上传用户头像
     *
     * @param file 头像文件
     * @return 头像访问地址
     */
    ApiResult<String> uploadFile(MultipartFile file);

    /**
     * 获取当前用户头像
     *
     * @return 头像访问地址
     */
    ApiResult<String> getAvatar();

    /**
     * 获取当前用户邮箱
     *
     * @return 邮箱地址
     */
    ApiResult<String> getEmail();

    /**
     * 获取当前用户在数据库中的最新状态（不读 JWT）
     *
     * @return 最新 status：0 待审核 1 启用 2 禁用
     */
    ApiResult<Integer> getStatus();

    /**
     * 获取当前登录用户资料
     *
     * @return 用户资料
     */
    ApiResult<UserProfileVO> getProfile();

    /**
     * 更新当前登录用户资料（仅 realName/phone/email/gender）
     *
     * @param request 更新参数
     * @return 操作结果
     */
    ApiResult updateProfile(UserProfileUpdateRequest request);

    /**
     * 当前用户修改密码（校验旧密码后更新，并清除登录态）
     *
     * @param request 密码参数
     * @return 操作结果
     */
    ApiResult changePassword(UserPasswordUpdateRequest request);
}
