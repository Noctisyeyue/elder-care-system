package com.eldercare.system.service;

import com.eldercare.system.entity.OperationLog;

/**
 * 操作审计日志服务接口。
 *
 * <p>第一阶段只提供异步保存能力，不做查询。
 */
public interface OperationLogService {

    /**
     * 异步保存操作审计日志（不阻塞业务线程）。
     *
     * @param log 操作日志实体
     */
    void saveAsync(OperationLog log);
}
