package com.eldercare.system.dto.user;

import lombok.Data;

/**
 * 用户列表查询请求
 */
@Data
public class UserListRequest {

    private String userName; // 用户名，支持模糊查询
    private int pageNum; // 当前页码
    private int pageSize; // 每页数量
}
