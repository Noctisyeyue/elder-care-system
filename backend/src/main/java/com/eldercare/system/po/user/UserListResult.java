package com.eldercare.system.po.user;

import lombok.Data;

import java.util.List;

@Data
public class UserListResult {
    private List<UserResult> list;
    private int total;
    public UserListResult(List<UserResult> list, int total) {
        this.list = list;
        this.total = total;
    }
}
