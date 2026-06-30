package com.eldercare.system.vo.user;

import lombok.Data;

import java.util.List;

/**
 * 用户列表结果视图
 */
@Data
public class UserListResultVO {

    private List<UserResultVO> list; // 用户列表
    private int total; // 总记录数

    /**
     * 创建用户列表结果视图
     *
     * @param list 用户列表
     * @param total 总记录数
     */
    public UserListResultVO(List<UserResultVO> list, int total) {
        this.list = list;
        this.total = total;
    }
}
