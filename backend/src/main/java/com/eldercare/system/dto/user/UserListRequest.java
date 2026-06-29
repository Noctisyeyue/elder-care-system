package com.eldercare.system.dto.user;

import lombok.Data;

@Data
public class UserListRequest {
    private String userName;
    private int pageNum;
    private int pageSize;
}
