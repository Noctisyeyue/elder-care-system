package com.eldercare.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 套餐客户映射
 */
@TableName("set_meal_customer_mapping")
@Data
public class SetMealCustomerMapping extends BaseEntity {

    /** 映射记录ID */
    @TableId(value = "set_meal_customer_mapping_id", type = IdType.AUTO)
    private Long setMealCustomerMappingId;

    /** 套餐ID */
    private Long setMealId;

    /** 客户ID */
    private Long customerId;

    /** 日期 */
    private String date;
}
