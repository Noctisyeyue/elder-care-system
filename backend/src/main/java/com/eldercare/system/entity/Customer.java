package com.eldercare.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 客户
 */
@TableName("Customer")
@Data
@EqualsAndHashCode(callSuper = false)
public class Customer extends BaseEntity {

    /** 客户ID */
    @TableId(value = "customer_id", type = IdType.AUTO)
    private Long customerId;

    /** 客户姓名 */
    private String customerName;

    /** 年龄 */
    private Integer age;

    /** 性别 */
    private String gender;

    /** 民族 */
    private String nation;

    /** 血型 */
    private String bloodType;

    /** 家属 */
    private String familyMember;

    /** 家属联系电话 */
    private String familyMemberTel;

    /** 客户联系电话 */
    private String tel;

    /** 身份证号 */
    private String idNumber;

    /** 床位ID */
    private Long bedId;

    /** 出生日期 */
    private String dateOfBirth;

    /** 入住时间 */
    private String checkInDate;

    /** 护理级别ID */
    private Long nursingLevelId;

    /** 合同到期时间 */
    private String contractEndDate;

    /** 客户类型（0自理老人 1护理老人） */
    private String customerType;

    /** 健康管家的用户ID */
    private Long userId;

    /** 身心状况 */
    private String physicalMentalStatus;

    /** 历史记录标识（0使用记录 1正在使用） */
    private String history;

    /** 客户头像 */
    private String img;
}
