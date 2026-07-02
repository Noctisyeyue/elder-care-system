package com.eldercare.system.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 操作审计注解：标注在 Controller 方法上，自动记录操作审计日志。
 *
 * <p>由 OperationLogAspect 拦截处理，异步写入 operation_log 表。
 *
 * <p>targetId 使用 SpEL 表达式显式声明操作对象，避免"猜"错：
 * <ul>
 *   <li>简单参数：{@code @OperationLog(..., targetId = "#userId")}</li>
 *   <li>DTO 字段：{@code @OperationLog(..., targetId = "#param.customerId")}</li>
 *   <li>Map 参数：{@code @OperationLog(..., targetId = "#body['customerId']")}</li>
 * </ul>
 * 不写 targetId 时该字段为空，不自动推断。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OperationLog {

    /** 业务模块，如"用户管理"、"客户管理" */
    String module();

    /** 操作名称，如"禁用用户"、"删除客户" */
    String operation();

    /** 操作类型，如 DISABLE、DELETE、UPDATE、ADD */
    String actionType();

    /**
     * 操作对象 ID 的 SpEL 表达式，如 "#userId"、"#param.customerId"。
     * 留空时 target_id 为 null，不自动推断（防止猜错）。
     */
    String targetId() default "";
}
