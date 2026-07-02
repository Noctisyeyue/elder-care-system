package com.eldercare.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eldercare.system.entity.OperationLog;

/**
 * 操作审计日志 Mapper。
 *
 * <p>继承 MyBatis-Plus BaseMapper，第一阶段只需要 insert 能力。
 */
public interface OperationLogMapper extends BaseMapper<OperationLog> {
}
