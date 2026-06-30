package com.eldercare.system.vo.caregiver;

import lombok.Data;

/**
 * 健康管家视图
 */
@Data
public class CaregiverVO {

    private Long id; // 健康管家用户 ID
    private String name; // 姓名
    private String phone; // 手机号
    private String gender; // 性别
    private String email; // 邮箱
}
