package com.eldercare.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eldercare.system.entity.NursingLevelItemMapping;

/** NursingLevelItemMappingMapper */
public interface NursingLevelItemMappingMapper extends BaseMapper<NursingLevelItemMapping> {
    void removeMapperByItemIdAndLevelId(NursingLevelItemMapping nursingLevelItemMapping);

    void removeByLevelId(Long levelId);

    void removeByItemId(Long itemId);

    void removeByNursingItemId(Long id);
}
