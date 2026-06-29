package com.eldercare.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eldercare.system.entity.SetMeal;

import java.util.List;

/** SetMealMapper */
public interface SetMealMapper extends BaseMapper<SetMeal> {
    List<SetMeal> selectSetMealsByDietCalendarId(Long dietCalendarId);
}
