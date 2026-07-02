package com.eldercare.system.service.impl;

import com.eldercare.system.entity.OperationLog;
import com.eldercare.system.mapper.OperationLogMapper;
import com.eldercare.system.service.OperationLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 操作审计日志服务实现。
 *
 * <p>使用 @Async("taskExecutor") 异步写入数据库，避免拖慢业务接口响应。
 * 写入失败时只记录错误日志，不影响主业务。
 */
@Slf4j
@Service
public class OperationLogServiceImpl implements OperationLogService {

    @Autowired
    private OperationLogMapper operationLogMapper;

    @Override
    @Async("taskExecutor")
    public void saveAsync(OperationLog operationLog) {
        try {
            operationLogMapper.insert(operationLog);
        } catch (Exception e) {
            log.error("操作审计日志写入失败: module={}, operation={}, error={}",
                    operationLog.getModule(), operationLog.getOperation(), e.getMessage(), e);
        }
    }
}
