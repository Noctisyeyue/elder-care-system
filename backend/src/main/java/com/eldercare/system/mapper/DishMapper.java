package com.eldercare.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eldercare.system.entity.Dish;

import java.util.List;

/** DishMapper */
public interface DishMapper extends BaseMapper<Dish> {

    List<Dish> availableDishes(String time, String pork);

    void updateImg(String upload, String name);

    String selectByName(String name);

    List<Dish> selectDishBysetMealRecordIdAndTime(Long setMealRecordId, String time);

    List<Dish> selectDishByCustomerIdAndDateAndTime(Long customerId, String date, String time);
}
