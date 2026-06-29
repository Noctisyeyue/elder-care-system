package com.eldercare.system.service;

import com.eldercare.system.po.user.*;
import com.eldercare.system.util.ApiResult;
import com.eldercare.system.po.user.result.RoleNumResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/** 用户服务接口 */
public interface UserService {
    ApiResult<LoginResponse> login(UserLogin user);

    ApiResult add(UserAdd user);

    ApiResult<UserListResult> list(UserList user);

    ApiResult delete(List<String> userName);

    ApiResult update(UserAdd user);

    ApiResult<Long> count();

    ApiResult<List<RoleNumResult>> roleNum();

    boolean getUserByEmail(String email);

    ApiResult<String> uploadFile(MultipartFile file,String token);

    ApiResult<String> getAvatar(String token);

    ApiResult<String> getEmail(String token);
}
