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

    /**
     * 查询指定月份的膳食日历
     *
     * @param params 月份查询参数
     * @return 膳食日历列表
     */
    ApiResult<List<DietCalendarResult>> monthList(DietCalendarMonthListParams params);

    /**
     * 查询指定餐次和忌口类型的菜品选项
     *
     * @param time 餐次
     * @param pork 忌口类型
     * @return 菜品选项列表
     */
    ApiResult<List<DishResult>> timeDishOptions(String time, String pork);

    /**
     * 分页查询菜品列表
     *
     * @param params 菜品查询参数
     * @return 菜品列表
     */
    ApiResult<DishListResult> dishList(DishListParams params);

    /**
     * 新增菜品
     *
     * @param params 菜品参数
     * @return 操作结果
     */
    ApiResult addDish(DishParams params);

    /**
     * 修改菜品
     *
     * @param params 菜品参数
     * @return 操作结果
     */
    ApiResult updateDish(DishParams params);

    /**
     * 删除菜品
     *
     * @param params 菜品删除参数
     * @return 操作结果
     */
    ApiResult removeDish(RemoveDishParams params);

    /**
     * 查询客户膳食分配列表
     *
     * @param params 客户查询参数
     * @return 客户膳食分配列表
     */
    ApiResult<ListResult<CustomerDishListResult>> customerList(CustomerListParams params);

    /**
     * 查询可选菜品
     *
     * @param time 餐次
     * @param pork 忌口类型
     * @return 可选菜品列表
     */
    ApiResult<List<DishResult>> availableDishes(String time, String pork);

    /**
     * 上传菜品图片
     *
     * @param file 图片文件
     * @param name 图片名称
     * @return 上传结果
     */
    ApiResult uploadImg(MultipartFile file, String name);

    /**
     * 分页查询套餐列表
     *
     * @param status      套餐状态
     * @param pork        忌口类型
     * @param setMealName 套餐名称
     * @param pageNum     页码
     * @param pageSize    每页大小
     * @return 套餐列表
     */
    ApiResult<ListResult<SetMealResult>> getSetMealList(String status, String pork, String setMealName, Integer pageNum, Integer pageSize);

    /**
     * 保存膳食日历
     *
     * @param params 膳食日历保存参数
     * @return 操作结果
     */
    ApiResult saveDietCalendar(DietCalendarSaveParams params);

    /**
     * 修改套餐
     *
     * @param setMeal 套餐信息
     * @return 操作结果
     */
    ApiResult updateSetMeal(SetMeal setMeal);

    /**
     * 删除套餐
     *
     * @param setMealId 套餐ID
     * @return 操作结果
     */
    ApiResult removeSetMeal(Long setMealId);

    /**
     * 保存套餐菜品配置
     *
     * @param params 套餐菜品配置参数
     * @return 操作结果
     */
    ApiResult saveSetMealDishes(SaveSetMealDishesParams params);

    /**
     * 保存客户套餐分配
     *
     * @param params 客户套餐分配参数
     * @return 操作结果
     */
    ApiResult saveCustomerSetMeal(SaveCustomerSetMealParams params);

    /**
     * 查询指定日期的套餐列表
     *
     * @param date 日期字符串
     * @return 套餐列表
     */
    ApiResult<List<SetMeal>> getDailyList(String date);
}
