package com.eldercare.system.vo.user;

import lombok.Data;

import java.util.List;

@Data
public class UserListResultVO {
    private List<UserResultVO> list;
    private int total;
    public UserListResultVO(List<UserResultVO> list, int total) {
        this.list = list;
        this.total = total;
    }
}
