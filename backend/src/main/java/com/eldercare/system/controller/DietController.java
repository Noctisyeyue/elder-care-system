package com.eldercare.system.controller;

import com.eldercare.system.entity.SetMeal;
import com.eldercare.system.po.ListResult;
import com.eldercare.system.util.ApiResult;
import com.eldercare.system.po.diet.params.*;
import com.eldercare.system.po.diet.result.*;
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
    public ApiResult<List<DietCalendarResult>> monthList(DietCalendarMonthListParams params){
        return dietService.monthList(params);
    }

    @Operation(summary = "保存某天的套餐配置信息")
    @PostMapping("/saveDietCalendar")
    public ApiResult saveDietCalendar(@RequestBody DietCalendarSaveParams params){
        return dietService.saveDietCalendar(params);
    }

    @Operation(summary = "获取套餐信息列表")
    @GetMapping("/setMeal/list")
    public ApiResult<ListResult<SetMealResult>> setMealList(String status, String pork, String setMealName, Integer pageNum, Integer pageSize){
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
    public ApiResult removeSetMeal(@RequestBody Long setMealId){
        return dietService.removeSetMeal(setMealId);
    }

    @Operation(summary = "获取某个时间段的可选菜品列表")
    @GetMapping("/timeFoodOptions")
    public ApiResult<List<DishResult>> timeDishOptions(String time, String pork){
        return dietService.timeDishOptions(time, pork);
    }

    @Operation(summary = "获取菜品信息列表")
    @GetMapping("/foodList")
    public ApiResult<DishListResult> dishList(DishListParams params){
        return dietService.dishList(params);
    }

    @Operation(summary = "添加菜品")
    @PostMapping("/addFood")
    public ApiResult addDish(@RequestBody DishParams params){
        return dietService.addDish(params);
    }

    @Operation(summary = "修改菜品")
    @PutMapping("/updateFood")
    public ApiResult updateDish(@RequestBody DishParams params){
        return dietService.updateDish(params);
    }

    @Operation(summary = "删除菜品")
    @PostMapping("/deleteFood")
    public ApiResult removeDish(@RequestBody RemoveDishParams params){
        return dietService.removeDish(params);
    }

    @Operation(summary = "获取客户菜品列表")
    @GetMapping("/customerList")
    public ApiResult<ListResult<CustomerDishListResult>> customerList(CustomerListParams params){
        return dietService.customerList(params);
    }

    @Operation(summary = "保存客户套餐配置")
    @PostMapping("/saveCustomerSetMeal")
    public ApiResult saveCustomerSetMeal(@RequestBody SaveCustomerSetMealParams params){
        return dietService.saveCustomerSetMeal(params);
    }

    @Operation(summary = "获取可用的菜品列表")
    @GetMapping("/availableDishes")
    public ApiResult<List<DishResult>> availableDishes(String time,String pork){
        return dietService.availableDishes(time,pork);
    }

    @Operation(summary = "保存套餐菜品配置")
    @PostMapping("/saveSetMealDishes")
    public ApiResult saveSetMealDishes(@RequestBody SaveSetMealDishesParams params){
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
}
