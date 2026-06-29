package com.eldercare.system.po.user;

import lombok.Data;

@Data
public class UserList {
    private String userName;
    private int pageNum;
    private int pageSize;
}
