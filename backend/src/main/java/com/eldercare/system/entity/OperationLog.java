package com.eldercare.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作审计日志实体，对应 operation_log 表。
 *
 * <p>不继承 BaseEntity，因为操作日志不需要逻辑删除、修改人等审计字段，
 * 只需要记录创建时间。
 */
@TableName("operation_log")
@Data
public class OperationLog {

    /** 操作日志ID */
    @TableId(value = "operation_log_id", type = IdType.AUTO)
    private Long operationLogId;

    /** 业务模块，如"用户管理" */
    private String module;

    /** 操作名称，如"禁用用户" */
    private String operation;

    /** 操作类型，如 DISABLE、DELETE */
    private String actionType;

    /** HTTP 请求方法，如 POST */
    private String requestMethod;

    /** 请求路径，如 /user/disable */
    private String requestUri;

    /** 操作人用户ID */
    private Long operatorId;

    /** 操作人邮箱 */
    private String operatorEmail;

    /** 操作人角色标识 */
    private String operatorRoleKey;

    /** 操作对象ID（如被禁用的用户ID） */
    private String targetId;

    /** 脱敏后的请求参数摘要（JSON 格式） */
    private String requestParams;

    /** 接口返回的业务码 */
    private Integer resultCode;

    /** 接口返回的消息 */
    private String resultMessage;

    /** 是否成功：1 成功，0 失败 */
    private Integer success;

    /** 异常信息（失败时记录） */
    private String errorMessage;

    /** 客户端 IP */
    private String clientIp;

    /** 耗时（毫秒） */
    private Long costMs;

    /** 创建时间 */
    private LocalDateTime createTime;
}
