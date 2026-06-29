package com.eldercare.system.service;

import com.eldercare.system.po.user.*;
import com.eldercare.system.util.ApiResult;
import com.eldercare.system.po.user.result.RoleNumResult;
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
    ApiResult<LoginResponse> login(UserLogin user);

    /**
     * 新增用户
     *
     * @param user 用户新增参数
     * @return 操作结果
     */
    ApiResult add(UserAdd user);

    /**
     * 分页查询用户列表
     *
     * @param user 查询参数
     * @return 用户列表
     */
    ApiResult<UserListResult> list(UserList user);

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
    ApiResult update(UserAdd user);

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
    ApiResult<List<RoleNumResult>> roleNum();

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
     * @param file  头像文件
     * @param token 登录令牌
     * @return 头像访问地址
     */
    ApiResult<String> uploadFile(MultipartFile file,String token);

    /**
     * 获取当前用户头像
     *
     * @param token 登录令牌
     * @return 头像访问地址
     */
    ApiResult<String> getAvatar(String token);

    /**
     * 获取当前用户邮箱
     *
     * @param token 登录令牌
     * @return 邮箱地址
     */
    ApiResult<String> getEmail(String token);
}
