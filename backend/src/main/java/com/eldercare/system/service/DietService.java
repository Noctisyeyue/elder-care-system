package com.eldercare.system.service;

import com.eldercare.system.entity.SetMeal;
import com.eldercare.system.po.ListResult;
import com.eldercare.system.util.ApiResult;
import com.eldercare.system.po.diet.params.*;
import com.eldercare.system.po.diet.result.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/** 膳食服务接口 */
public interface DietService {
    ApiResult<List<DietCalendarResult>> monthList(DietCalendarMonthListParams params);

    ApiResult<List<DishResult>> timeDishOptions(String time, String pork);

    ApiResult<DishListResult> dishList(DishListParams params);

    ApiResult addDish(DishParams params);

    ApiResult updateDish(DishParams params);

    ApiResult removeDish(RemoveDishParams params);

    ApiResult<ListResult<CustomerDishListResult>> customerList(CustomerListParams params);

    ApiResult<List<DishResult>> availableDishes(String time, String pork);

    ApiResult uploadImg(MultipartFile file, String name);

    ApiResult<ListResult<SetMealResult>> getSetMealList(String status, String pork, String setMealName, Integer pageNum, Integer pageSize);

    ApiResult saveDietCalendar(DietCalendarSaveParams params);

    ApiResult updateSetMeal(SetMeal setMeal);

    ApiResult removeSetMeal(Long setMealId);

    ApiResult saveSetMealDishes(SaveSetMealDishesParams params);

    ApiResult saveCustomerSetMeal(SaveCustomerSetMealParams params);

    ApiResult<List<SetMeal>> getDailyList(String date);
}
