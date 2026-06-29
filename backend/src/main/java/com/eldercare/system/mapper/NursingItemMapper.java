package com.eldercare.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eldercare.system.entity.NursingItem;
import com.eldercare.system.dto.nursing.RenewRequest;

import java.util.List;
import java.util.Map;

/** NursingItemMapper */
public interface NursingItemMapper extends BaseMapper<NursingItem> {
    List<NursingItem> selectItemsByLevelId(Long levelId, Integer pageStart, Integer pageSize);

    void removeByItemId(long id);

    void renew(RenewRequest params);

    List<NursingItem> selectByCustomerId(Long customerId, String name, Integer pageStart, Integer pageSize, String status);

    Long selectCountByCustomerId(Long customerId, String status);

    Long selectCountByLevelId(Long levelId);
}
