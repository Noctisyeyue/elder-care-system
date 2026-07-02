package com.eldercare.system.controller;

import com.eldercare.system.entity.SetMeal;
import com.eldercare.system.util.PageResult;
import com.eldercare.system.util.ApiResult;
import com.eldercare.system.dto.diet.*;
import com.eldercare.system.vo.diet.*;
import com.eldercare.system.service.DietService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/** 膳食管理控制器 */
@Tag(name = "膳食管理接口")
@RestController
@RequestMapping("/diet")
public class DietController {
    @Autowired
    private DietService dietService;

    @Operation(summary = "获取某个月内每天的套餐配置信息")
    @GetMapping("/monthList")
    public ApiResult<List<DietCalendarVO>> monthList(DietCalendarMonthListRequest params){
        return dietService.monthList(params);
    }

    @Operation(summary = "保存某天的套餐配置信息")
    @PostMapping("/saveDietCalendar")
    public ApiResult saveDietCalendar(@RequestBody DietCalendarSaveRequest params){
        return dietService.saveDietCalendar(params);
    }

    @Operation(summary = "获取套餐信息列表")
    @GetMapping("/setMeal/list")
    public ApiResult<PageResult<SetMealVO>> setMealList(String status, String pork, String setMealName, Integer pageNum, Integer pageSize){
        return dietService.getSetMealList(status, pork, setMealName, pageNum, pageSize);
    }

    @Operation(summary = "获取某一天的套餐信息列表")
    @GetMapping("/setMeal/dailyList")
    public ApiResult<List<SetMeal>> dailyList(String date){
        return dietService.getDailyList(date);
    }

    @Operation(summary = "修改套餐信息")
    @PostMapping("/setMeal/update")
    public ApiResult updateSetMeal(@RequestBody SetMeal setMeal){
        return dietService.updateSetMeal(setMeal);
    }

    @Operation(summary = "删除套餐")
    @PostMapping("/setMeal/remove")
    public ApiResult removeSetMeal(@RequestParam Long setMealId){
        return dietService.removeSetMeal(setMealId);
    }

    @Operation(summary = "获取某个时间段的可选菜品列表")
    @GetMapping("/timeFoodOptions")
    public ApiResult<List<DishVO>> timeDishOptions(String time, String pork){
        return dietService.timeDishOptions(time, pork);
    }

    @Operation(summary = "获取菜品信息列表")
    @GetMapping("/foodList")
    public ApiResult<DishListVO> dishList(DishListRequest params){
        return dietService.dishList(params);
    }

    @Operation(summary = "添加菜品")
    @PostMapping("/addFood")
    public ApiResult addDish(@RequestBody DishRequest params){
        return dietService.addDish(params);
    }

    @Operation(summary = "修改菜品")
    @PutMapping("/updateFood")
    public ApiResult updateDish(@RequestBody DishRequest params){
        return dietService.updateDish(params);
    }

    @Operation(summary = "删除菜品")
    @PostMapping("/deleteFood")
    public ApiResult removeDish(@RequestBody RemoveDishRequest params){
        return dietService.removeDish(params);
    }

    @Operation(summary = "获取客户菜品列表")
    @GetMapping("/customerList")
    public ApiResult<PageResult<CustomerDishListVO>> customerList(CustomerListRequest params){
        return dietService.customerList(params);
    }

    @Operation(summary = "保存客户套餐配置")
    @PostMapping("/saveCustomerSetMeal")
    public ApiResult saveCustomerSetMeal(@RequestBody SaveCustomerSetMealRequest params){
        return dietService.saveCustomerSetMeal(params);
    }

    @Operation(summary = "获取可用的菜品列表")
    @GetMapping("/availableDishes")
    public ApiResult<List<DishVO>> availableDishes(String time,String pork){
        return dietService.availableDishes(time,pork);
    }

    @Operation(summary = "保存套餐菜品配置")
    @PostMapping("/saveSetMealDishes")
    public ApiResult saveSetMealDishes(@RequestBody SaveSetMealDishesRequest params){
        return dietService.saveSetMealDishes(params);
    }

    @Operation(summary = "上传菜品图片")
    @PostMapping("/img/upload")
    public ApiResult uploadImg(@RequestParam(name ="file", required = false) MultipartFile file,@RequestParam("name") String name){
        if(file != null)
            return dietService.uploadImg(file,name);
        ApiResult result = new ApiResult();
        result.setCode(200);
        return result;
    }

    @Operation(summary = "获取本周膳食配餐量统计")
    @GetMapping("/weeklyMealCount")
    public ApiResult<List<WeeklyMealCountVO>> weeklyMealCount() {
        return dietService.weeklyMealCount();
    }
}
