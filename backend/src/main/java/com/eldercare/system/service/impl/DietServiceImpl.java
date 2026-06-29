package com.eldercare.system.service.impl;
import com.eldercare.system.service.DietService;

import com.aliyuncs.exceptions.ClientException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eldercare.system.entity.*;
import com.eldercare.system.mapper.*;
import com.eldercare.system.po.ListResult;
import com.eldercare.system.util.ApiResult;
import com.eldercare.system.po.customer.customerresult.CustomerItem;
import com.eldercare.system.po.diet.params.*;
import com.eldercare.system.po.diet.result.*;
import com.eldercare.system.po.user.ImgUploadUtil;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class DietServiceImpl implements DietService{
    @Autowired
    private DietCalendarMapper dietCalendarMapper;
    @Autowired
    private DietCalendarSetMealMappingMapper dietCalendarSetMealMappingMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetMealMapper setMealMapper;
    @Autowired
    private SetMealCustomerMappingMapper setMealCustomerMappingMapper;
    @Autowired
    private SetMealRecordMapper setMealRecordMapper;
    @Autowired
    private SetMealRecordDishMappingMapper setMealRecordDishMappingMapper;
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private ImgUploadUtil imgUploadUtil;
    @Override
    public ApiResult<List<DietCalendarResult>> monthList(DietCalendarMonthListParams params) {
        // 获取指定月份的膳食日历
        // 变量准备
        ApiResult<List<DietCalendarResult>> result = new ApiResult<>();
        List<DietCalendarResult> data = new ArrayList<>();
        String month = params.getMonth() < 10? "0" + params.getMonth() : String.valueOf(params.getMonth());
        String date = params.getYear() + "-" + month + "%";
        // 数据库查询
        // 查找膳食日历
        List<DietCalendar> dietCalendars;
        QueryWrapper<DietCalendar> dietCalendarQueryWrapper = new QueryWrapper<>();
        dietCalendarQueryWrapper.like("date", date);
        dietCalendarQueryWrapper.eq("del_flag", "0");
        dietCalendarQueryWrapper.orderByAsc("date");
        try {
            dietCalendars = dietCalendarMapper.selectList(dietCalendarQueryWrapper);
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("查找膳食日历数据库错误");
            throw e;
        }
        // 遍历查询结果
        for(DietCalendar dietCalendar : dietCalendars){
            // 查找套餐
            // 创建结果对象
            DietCalendarResult dietCalendarResult = new DietCalendarResult();
            dietCalendarResult.setCalendarId(dietCalendar.getDietCalendarId());
            dietCalendarResult.setDate(dietCalendar.getDate());
            List<SetMealResult> setMealResults = new ArrayList<>();
            List<SetMeal> setMeals;
            // 根据膳食日历id查找套餐
            try {
                setMeals = setMealMapper.selectSetMealsByDietCalendarId(dietCalendar.getDietCalendarId());
            } catch (Exception e) {
                result.setCode(500);
                result.setMessage("根据膳食日历id查找套餐数据库错误");
                throw e;
            }
            // 遍历查询结果
            for(SetMeal setMeal : setMeals){
                // 查找套餐记录
                // 创建结果对象
                SetMealResult setMealResult = new SetMealResult();
                setMealResult.setSetMealId(setMeal.getSetMealId());
                setMealResult.setName(setMeal.getSetMealName());
                setMealResult.setPork(setMeal.getPork());
                List<SetMealRecord> setMealRecords;
                // 创建查询条件
                QueryWrapper<SetMealRecord> setMealRecordQueryWrapper = new QueryWrapper<>();
                setMealRecordQueryWrapper.eq("del_flag", "0");
                setMealRecordQueryWrapper.eq("set_meal_id", setMeal.getSetMealId());
                // 根据套餐id查询套餐记录
                try {
                    setMealRecords = setMealRecordMapper.selectList(setMealRecordQueryWrapper);
                } catch (Exception e) {
                    result.setCode(500);
                    result.setMessage("根据套餐id查询套餐记录数据库错误");
                    throw e;
                }
                // 遍历查询结果
                for (SetMealRecord setMealRecord : setMealRecords) {
                    // 获取套餐记录对应菜品
                    ApiResult<List<DishResult>> timeResult;
                    switch (setMealRecord.getTime()){
                        case "0":
                            timeResult = selectDishBysetMealRecordIdAndTime(setMealRecord);
                            List<DishResult> breakfast = timeResult.getData();
                            if(timeResult.getCode() != 200){
                                result.setCode(500);
                                result.setMessage("查找套餐记录对应菜品数据库错误");
                            }
                            setMealResult.setBreakfast(breakfast);
                        break;
                        case "1":
                            timeResult = selectDishBysetMealRecordIdAndTime(setMealRecord);
                            List<DishResult> lunch = timeResult.getData();
                            if(timeResult.getCode() != 200){
                                result.setCode(500);
                                result.setMessage("查找套餐记录对应菜品数据库错误");
                            }
                            setMealResult.setLunch(lunch);
                        break;
                        case "2":
                            timeResult = selectDishBysetMealRecordIdAndTime(setMealRecord);
                            List<DishResult> dinner = timeResult.getData();
                            if(timeResult.getCode() != 200){
                                result.setCode(500);
                                result.setMessage("查找套餐记录对应菜品数据库错误");
                            }
                            setMealResult.setDinner(dinner);
                        break;
                    }
                }
                setMealResults.add(setMealResult);
            }
            dietCalendarResult.setSetMeals(setMealResults);
            data.add(dietCalendarResult);
        }
        // 数据包装并返回
        result.setCode(200);
        result.setData(data);
        result.setMessage("查询成功");
        return result;
    }

    private ApiResult<List<DishResult>> selectDishBysetMealRecordIdAndTime(SetMealRecord setMealRecord){
        ApiResult<List<DishResult>> result = new ApiResult<>();
        List<Dish> dbDishes;
        List<DishResult> data = new ArrayList<>();
        try {
            dbDishes = dishMapper.selectDishBysetMealRecordIdAndTime(setMealRecord.getSetMealRecordId(), setMealRecord.getTime());
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("查找套餐记录对应菜品数据库错误");
            throw e;
        }
        for (Dish dish : dbDishes){
            DishResult dishResult = new DishResult();
            dishResult.setDishId(dish.getDishId());
            dishResult.setName(dish.getDishName());
            dishResult.setCategory(dish.getDishType());
            dishResult.setTime(dish.getTime());
            dishResult.setPork(dish.getPork());
            dishResult.setImg(dish.getImg());
            data.add(dishResult);
        }
        result.setCode(200);
        result.setData(data);
        result.setMessage("查询成功");
        return result;
    }

    @Override
    public ApiResult<List<DishResult>> timeDishOptions(String time, String pork) {
        // 获取所有可用菜品
        // 变量准备
        ApiResult<List<DishResult>> result = new ApiResult<>();
        List<DishResult> data = new ArrayList<>();
        // 数据库查询
        List<Dish> dbDishes;
        QueryWrapper<Dish> dishQueryWrapper = new QueryWrapper<>();
        dishQueryWrapper.eq("time", time);
        if (Objects.equals(pork, "1")){
            dishQueryWrapper.eq("pork", pork);
        }
        dishQueryWrapper.eq("status", "0");
        dishQueryWrapper.eq("del_flag", "0");
        try {
            dbDishes = dishMapper.selectList(dishQueryWrapper);
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("获取所有可用菜品数据库错误");
            throw e;
        }
        for (Dish dish : dbDishes){
            DishResult dishResult = new DishResult();
            dishResult.setDishId(dish.getDishId());
            dishResult.setName(dish.getDishName());
            data.add(dishResult);
        }
        // 数据包装并返回
        result.setCode(200);
        result.setData(data);
        result.setMessage("查询成功");
        return result;
    }

    @Override
    public ApiResult<DishListResult> dishList(DishListParams params) {
        // 按菜品名获取菜品列表
        // 变量准备
        ApiResult<DishListResult> result = new ApiResult<>();
        DishListResult data = new DishListResult();
        List<DishResult> list = new ArrayList<>();
        // 数据库查询
        List<Dish> dbDishes;
        QueryWrapper<Dish> dishQueryWrapper = new QueryWrapper<>();
        dishQueryWrapper.like("dish_name", "%"+params.getName()+"%");
        if (params.getPork() != null){
            dishQueryWrapper.eq("pork", params.getPork());
        }
        dishQueryWrapper.eq("del_flag", "0");
        dishQueryWrapper.orderByDesc("dish_id");
        IPage<Dish> page = new Page<>(params.getPageNum(), params.getPageSize());
        try {
            dbDishes = dishMapper.selectList(page, dishQueryWrapper);
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("按菜品名获取菜品列表数据库错误");
            throw e;
        }
        // 将数据包装成前端所需数据格式
        for (Dish dish : dbDishes){
            DishResult dishResult = new DishResult();
            dishResult.setDishId(dish.getDishId());
            if(dish.getImg() != null)
                dishResult.setImg(dish.getImg());
            else
                dishResult.setImg("https://neuhealth.oss-cn-beijing.aliyuncs.com/empty.png");
            dishResult.setName(dish.getDishName());
            String catogory = switch (dish.getDishType()) {
                case "0" -> "素菜";
                case "1" -> "荤菜";
                case "2" -> "主食";
                case "3" -> "饮品";
                default -> "未知";
            };
            dishResult.setCategory(catogory);
            dishResult.setPork(dish.getPork());
            dishResult.setTime(dish.getTime());
            String status = switch (dish.getStatus()) {
                case "0" -> "on";
                case "1" -> "off";
                default -> "未知";
            };
            dishResult.setStatus(status);
            list.add(dishResult);
        }
        data.setList(list);
        Long total = dishMapper.selectCount(dishQueryWrapper);
        data.setTotal(Math.toIntExact(total));
        // 数据包装并返回
        result.setCode(200);
        result.setData(data);
        result.setMessage("查询成功");
        return result;
    }

    @SneakyThrows
    @Override
    public ApiResult addDish(DishParams params) {
        // 添加菜品
        // 变量准备
        ApiResult result = new ApiResult();
        Dish dish = new Dish();
        dish.setDishName(params.getName());
        String status = switch (params.getStatus()){
            case "on" -> "0";
            case "off" -> "1";
            default -> "9";
        };
        dish.setStatus(status);
        dish.setPork(params.getPork());
        dish.setTime(params.getTime());
        String dishType = switch (params.getCategory()){
            case "素菜" -> "0";
            case "荤菜" -> "1";
            case "主食" -> "2";
            case "饮品" -> "3";
            default -> "9";
        };
        dish.setDishType(dishType);
        // 数据库查询
        Dish dbDish;
        QueryWrapper<Dish> dishQueryWrapper = new QueryWrapper<>();
        dishQueryWrapper.eq("dish_name", params.getName());
        dishQueryWrapper.eq("time", params.getTime());
        dishQueryWrapper.eq("del_flag", "0");
        try {
            dbDish = dishMapper.selectOne(dishQueryWrapper);
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("查询菜品数据库错误");
            throw e;
        }
        if(dbDish != null){
            result.setCode(501);
            result.setMessage("菜品已存在");
            return result;
        }
        // 数据库操作
        try {
            dishMapper.insert(dish);
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("添加菜品数据库错误");
            throw e;
        }
        // 数据包装并返回
        result.setCode(200);
        result.setMessage("添加成功");
        return result;
    }

    @SneakyThrows
    @Override
    public ApiResult updateDish(DishParams params) {
        // 修改菜品
        // 变量准备
        ApiResult result = new ApiResult();
        Dish dish = new Dish();
        dish.setDishId(params.getDishId());
        dish.setDishName(params.getName());
        String status = switch (params.getStatus()){
            case "on" -> "0";
            case "off" -> "1";
            default -> "9";
        };
        dish.setStatus(status);
        dish.setTime(params.getTime());
        dish.setPork(params.getPork());
        String dishType = switch (params.getCategory()){
            case "素菜" -> "0";
            case "荤菜" -> "1";
            case "主食" -> "2";
            case "饮品" -> "3";
            default -> "9";
        };
        dish.setDishType(dishType);
        // 数据库查询
        Dish dbDish;
        try {
            dbDish = dishMapper.selectById(params.getDishId());
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("查询菜品数据库错误");
            throw e;
        }
        if(dbDish == null){
            result.setCode(502);
            result.setMessage("菜品不存在");
            return result;
        }
        if(dishMapper.selectByName(params.getName())!= null&& !Objects.equals(dishMapper.selectByName(params.getName()), dbDish.getDishName())){
            result.setCode(503);
            result.setMessage("菜品已存在,请更换名称");
            return result;
        }
        // 数据库操作
        try {
            dishMapper.updateById(dish);
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("修改菜品数据库错误");
            throw e;
        }
        // 数据包装并返回
        result.setCode(200);
        result.setMessage("修改成功");
        return result;
    }

    @Override
    public ApiResult removeDish(RemoveDishParams params) {
        // 删除菜品
        // 变量准备
        ApiResult result = new ApiResult();
        List<Long> ids = params.getId();
        for (Long id : ids){
            // 数据库查询
            Dish dbDish;
            try {
                dbDish = dishMapper.selectById(id);
            } catch (Exception e) {
                result.setCode(500);
                result.setMessage("查询菜品数据库错误");
                throw e;
            }
            if(dbDish == null){
                result.setCode(502);
                result.setMessage("菜品不存在");
                return result;
            }
            // 数据库操作
            dbDish.setDelFlag("1");
            try {
                dishMapper.updateById(dbDish);
            } catch (Exception e) {
                result.setCode(500);
                result.setMessage("删除菜品数据库错误");
                throw e;
            }
        }
        // 数据包装并返回
        result.setCode(200);
        result.setMessage("删除成功");
        return result;
    }

    @Override
    public ApiResult<ListResult<CustomerDishListResult>> customerList(CustomerListParams params) {
        // 获取客户菜品列表列表
        // 变量准备
        ApiResult<ListResult<CustomerDishListResult>> result = new ApiResult<>();
        ListResult<CustomerDishListResult> data = new ListResult<>();
        List<CustomerDishListResult> list = new ArrayList<>();
        List<Customer> customers;
        // 数据库查询
        QueryWrapper<Customer> customerQueryWrapper = new QueryWrapper<>();
        customerQueryWrapper.eq("del_flag", "0");
        customerQueryWrapper.like("customer_name", "%"+params.getCustomerName()+"%");
        customerQueryWrapper.orderByDesc("customer_id");
        IPage<Customer> page = new Page<>(params.getPageNum(), params.getPageSize());
        // 获取客户信息
        System.out.println(params);
        if(params.getPork() == null){

        } else if (params.getPork() == 0){
            customerQueryWrapper.ne("nation", "回");
        } else if (params.getPork() == 1){
            customerQueryWrapper.eq("nation", "回");
        } else {
            result.setCode(503);
            result.setMessage("参数错误");
            return result;
        }
        try {
            customers = customerMapper.selectList(page, customerQueryWrapper);
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("获取客户信息数据库错误");
            throw e;
        }
        // 遍历查询结果
        for (Customer customer : customers){
            CustomerDishListResult customerDishListResult = new CustomerDishListResult();
            customerDishListResult.setCustomerId(customer.getCustomerId());
            customerDishListResult.setCustomerName(customer.getCustomerName());
            customerDishListResult.setAge(customer.getAge());
            customerDishListResult.setNation(customer.getNation());
            customerDishListResult.setGender(customer.getGender());
            // 获取早餐
            List<DishResult> breakfast = selectDishByCustomerIdAndDateAndTime(customer.getCustomerId(), params.getDate(), "0");
            customerDishListResult.setBreakfast(breakfast);
            // 获取午餐
            List<DishResult> lunch = selectDishByCustomerIdAndDateAndTime(customer.getCustomerId(), params.getDate(), "1");
            customerDishListResult.setLunch(lunch);
            // 获取晚餐
            List<DishResult> dinner = selectDishByCustomerIdAndDateAndTime(customer.getCustomerId(), params.getDate(), "2");
            customerDishListResult.setDinner(dinner);
            list.add(customerDishListResult);
        }
        // 数据包装并返回
        data.setRecords(list);
        Long total = customerMapper.selectCount(customerQueryWrapper);
        data.setTotal(total);
        result.setCode(200);
        result.setData(data);
        result.setMessage("查询成功");
        return result;
    }

    private List<DishResult> selectDishByCustomerIdAndDateAndTime(Long customerId, String date, String time) {
        // 获取指定客户指定日期指定时间段内的菜品
        // 变量准备
        List<Dish> timeDishes;
        List<DishResult> result = new ArrayList<>();
        // 数据库查询
        try {
            timeDishes = dishMapper.selectDishByCustomerIdAndDateAndTime(customerId, date, time);
        } catch (Exception e) {
            throw e;
        }
        if (timeDishes.isEmpty()){
            return result;
        }
        for (Dish dish : timeDishes){
            DishResult dishResult = new DishResult();
            dishResult.setDishId(dish.getDishId());
            dishResult.setName(dish.getDishName());
            String category = switch (dish.getDishType()){
                case "0" -> "素菜";
                case "1" -> "荤菜";
                case "2" -> "主食";
                case "3" -> "饮品";
                default -> "未知";
            };
            dishResult.setCategory(category);
            result.add(dishResult);
        }
        return result;
    }

    @Override
    public ApiResult<List<DishResult>> availableDishes(String time, String pork) {
        // 获取可用的膳食列表
        // 变量准备
        ApiResult<List<DishResult>> result = new ApiResult<>();
        List<DishResult> data = new ArrayList<>();
        List<Dish> dishes;
        // 数据库查询
        try {
            dishes = dishMapper.availableDishes(time, pork);
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("获取可用膳食列表数据库错误");
            throw e;
        }
        if (Objects.equals(pork, "0")){
            try {
                dishes.addAll(dishMapper.availableDishes(time, "1"));
            } catch (Exception e) {
                result.setCode(500);
                result.setMessage("获取可用膳食列表数据库错误");
                throw e;
            }
        }
        for (Dish dish : dishes){
            DishResult dishResult = new DishResult();
            dishResult.setDishId(dish.getDishId());
            dishResult.setName(dish.getDishName());
            String category = switch (dish.getDishType()){
                case "0" -> "素菜";
                case "1" -> "荤菜";
                case "2" -> "主食";
                case "3" -> "饮品";
                default -> "未知";
            };
            dishResult.setCategory(category);
            String status = switch (dish.getStatus()){
                case "0" -> "启用";
                case "1" -> "停用";
                default -> "未知";
            };
            dishResult.setStatus(status);
            data.add(dishResult);
        }
        // 数据包装并返回
        result.setCode(200);
        result.setData(data);
        result.setMessage("查询成功");
        return result;
    }

    @Override
    public ApiResult uploadImg(MultipartFile file, String name) {
        ApiResult  result = new ApiResult();
        try {
            String originalFilename = file.getOriginalFilename();
            String extendName = originalFilename.substring(originalFilename.lastIndexOf("."));
            String name1 = UUID.randomUUID().toString() + extendName;
            String upload = imgUploadUtil.upload(file.getBytes(), name1, file.getContentType());
            dishMapper.updateImg(upload, name);
            result.setCode(200);
            result.setMessage("上传成功");
            return result;
        } catch (IOException e) {
            result.setCode(500);
            System.out.println("e: " + e);
            result.setMessage("上传失败");
            return result;
        } catch (ClientException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ApiResult<ListResult<SetMealResult>> getSetMealList(String status, String pork, String setMealName, Integer pageNum, Integer pageSize) {
        // 获取套餐列表
        // 变量准备
        ApiResult<ListResult<SetMealResult>> result = new ApiResult<>();
        ListResult<SetMealResult> data = new ListResult<>();
        List<SetMealResult> setMealResults = new ArrayList<>();
        List<SetMeal> setMeals;
        Long total;
        // 数据库查询
        QueryWrapper<SetMeal> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(status != null, "status", status);
        queryWrapper.eq(!Objects.equals(pork, "2"), "pork", pork);
        queryWrapper.like(setMealName != null, "set_meal_name", "%"+setMealName+"%");
        queryWrapper.eq("del_flag", "0");
        if(pageNum != null && pageSize != null){
            IPage<SetMeal> page = new Page<>(pageNum, pageSize);
            try {
                setMeals = setMealMapper.selectPage(page, queryWrapper).getRecords();
            } catch (Exception e) {
                result.setCode(500);
                result.setMessage("获取套餐列表数据库错误");
                throw e;
            }
        }else{
            try {
                setMeals = setMealMapper.selectList(queryWrapper);
            } catch (Exception e) {
                result.setCode(500);
                result.setMessage("获取套餐列表数据库错误");
                throw e;
            }
        }
        total = setMealMapper.selectCount(queryWrapper);
        // 遍历查询结果
        for (SetMeal setMeal : setMeals){
            SetMealResult setMealResult = new SetMealResult();
            setMealResult.setSetMealId(setMeal.getSetMealId());
            setMealResult.setName(setMeal.getSetMealName());
            String hui = switch (setMeal.getPork()) {
                case "0" -> "非清真";
                case "1" -> "清真";
                default -> "未知";
            };
            setMealResult.setPork(hui);
            setMealResult.setStatus(setMeal.getStatus());
            List<SetMealRecord> setMealRecords;
            // 创建查询条件
            QueryWrapper<SetMealRecord> setMealRecordQueryWrapper = new QueryWrapper<>();
            setMealRecordQueryWrapper.eq("del_flag", "0");
            setMealRecordQueryWrapper.eq("set_meal_id", setMeal.getSetMealId());
            // 根据套餐id查询套餐记录
            try {
                setMealRecords = setMealRecordMapper.selectList(setMealRecordQueryWrapper);
            } catch (Exception e) {
                result.setCode(500);
                result.setMessage("根据套餐id查询套餐记录数据库错误");
                throw e;
            }
            // 遍历查询结果
            for (SetMealRecord setMealRecord : setMealRecords) {
                ApiResult<List<DishResult>> timeResult;
                switch (setMealRecord.getTime()) {
                    case "0":
                        timeResult = selectDishBysetMealRecordIdAndTime(setMealRecord);
                        List<DishResult> breakfast = timeResult.getData();
                        if(timeResult.getCode() != 200){
                            result.setCode(500);
                            result.setMessage("查找套餐记录对应菜品数据库错误");
                        }
                        setMealResult.setBreakfast(breakfast);
                        break;
                    case "1":
                        timeResult = selectDishBysetMealRecordIdAndTime(setMealRecord);
                        List<DishResult> lunch = timeResult.getData();
                        if(timeResult.getCode() != 200){
                            result.setCode(500);
                            result.setMessage("查找套餐记录对应菜品数据库错误");
                        }
                        setMealResult.setLunch(lunch);
                        break;
                    case "2":
                        timeResult = selectDishBysetMealRecordIdAndTime(setMealRecord);
                        List<DishResult> dinner = timeResult.getData();
                        if(timeResult.getCode() != 200){
                            result.setCode(500);
                            result.setMessage("查找套餐记录对应菜品数据库错误");
                        }
                        setMealResult.setDinner(dinner);
                        break;
                }
            }
            setMealResults.add(setMealResult);
        }
        // 数据包装并返回
        data.setRecords(setMealResults);
        data.setTotal(total);
        System.out.println(data);
        result.setCode(200);
        result.setData(data);
        result.setMessage("查询成功");
        return result;
    }

    @Override
    public ApiResult saveDietCalendar(DietCalendarSaveParams params) {
        // 变量准备
        ApiResult result = new ApiResult();
        DietCalendarSetMealMapping huiDietCalendarSetMealMapping = new DietCalendarSetMealMapping();
        DietCalendarSetMealMapping notHuiDietCalendarSetMealMapping = new DietCalendarSetMealMapping();
        List<DietCalendar> dietCalendars;
        // 数据库操作
        try {
            dietCalendars = dietCalendarMapper.selectList(new QueryWrapper<DietCalendar>().eq("date", params.getDate()));
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("查找日历数据库错误");
            throw e;
        }
        QueryWrapper<DietCalendarSetMealMapping> queryWrapper = new QueryWrapper<>();
        if(dietCalendars.isEmpty()){
            DietCalendar dietCalendar = new DietCalendar();
            dietCalendar.setDate(params.getDate());
            try {
                dietCalendarMapper.insert(dietCalendar);
            } catch (Exception e) {
                result.setCode(500);
                result.setMessage("保存日历数据库错误");
                throw e;
            }
            DietCalendar dbDietCalendar;
            try {
                dbDietCalendar = dietCalendarMapper.selectOne(new QueryWrapper<DietCalendar>().eq("date", params.getDate()));
            } catch (Exception e) {
                result.setCode(500);
                result.setMessage("查找日历数据库错误");
                throw e;
            }
            huiDietCalendarSetMealMapping.setDietCalendarId(dbDietCalendar.getDietCalendarId());
            notHuiDietCalendarSetMealMapping.setDietCalendarId(dbDietCalendar.getDietCalendarId());
            queryWrapper.eq("diet_calendar_id", dbDietCalendar.getDietCalendarId());
        } else if (dietCalendars.size() == 1){
            huiDietCalendarSetMealMapping.setDietCalendarId(dietCalendars.get(0).getDietCalendarId());
            notHuiDietCalendarSetMealMapping.setDietCalendarId(dietCalendars.get(0).getDietCalendarId());
            queryWrapper.eq("diet_calendar_id", dietCalendars.get(0).getDietCalendarId());
        } else {
            result.setCode(502);
            result.setMessage("此日期有重复记录");
            return result;
        }
        List<DietCalendarSetMealMapping> dietCalendarSetMealMappings;
        try {
            dietCalendarSetMealMappings = dietCalendarSetMealMappingMapper.selectList(queryWrapper);
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("查找日历套餐映射数据库错误");
            throw e;
        }
        for (DietCalendarSetMealMapping dietCalendarSetMealMapping : dietCalendarSetMealMappings){
            dietCalendarSetMealMapping.setDelFlag("1");
            QueryWrapper<DietCalendarSetMealMapping> dietCalendarSetMealMappingQueryWrapper = new QueryWrapper<>();
            dietCalendarSetMealMappingQueryWrapper.eq("diet_calendar_id", dietCalendarSetMealMapping.getDietCalendarId());
            dietCalendarSetMealMappingQueryWrapper.eq("set_meal_id", dietCalendarSetMealMapping.getSetMealId());
            try {
                dietCalendarSetMealMappingMapper.update(dietCalendarSetMealMapping, dietCalendarSetMealMappingQueryWrapper);
            } catch (Exception e) {
                result.setCode(500);
                result.setMessage("删除日历套餐映射数据库错误");
                throw e;
            }
        }
        huiDietCalendarSetMealMapping.setSetMealId(params.getHuiSetMealId());
        try {
            dietCalendarSetMealMappingMapper.insert(huiDietCalendarSetMealMapping);
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("保存日历套餐映射数据库错误");
            throw e;
        }
        notHuiDietCalendarSetMealMapping.setSetMealId(params.getNotHuiSetMealId());
        try {
            dietCalendarSetMealMappingMapper.insert(notHuiDietCalendarSetMealMapping);
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("保存日历套餐映射数据库错误");
            throw e;
        }
        // 数据包装并返回
        result.setCode(200);
        result.setMessage("保存成功");
        return result;
    }

    @Override
    public ApiResult updateSetMeal(SetMeal setMeal) {
        // 添加或修改套餐
        // 变量准备
        ApiResult result = new ApiResult();
        SetMeal dbSetMeal;
        // 数据库查询
        // 数据库修改
        if (setMeal.getSetMealId() != null){
            try {
                dbSetMeal = setMealMapper.selectById(setMeal.getSetMealId());
            } catch (Exception e){
                result.setCode(500);
                result.setMessage("以套餐名查找套餐数据库错误");
                throw e;
            }
            if(dbSetMeal != null){
                try {
                    setMealMapper.updateById(setMeal);
                } catch (Exception e) {
                    result.setCode(500);
                    result.setMessage("更新套餐数据库错误");
                    throw e;
                }
            }
        }
        else {
            QueryWrapper<SetMeal> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("set_meal_name", setMeal.getSetMealName());
            try {
                dbSetMeal = setMealMapper.selectOne(queryWrapper);
            } catch (Exception e) {
                result.setCode(500);
                result.setMessage("以套餐名查找套餐数据库错误");
                throw e;
            }
            if (dbSetMeal != null && dbSetMeal.getDelFlag().equals("1")){
                setMeal.setSetMealId(dbSetMeal.getSetMealId());
                setMeal.setDelFlag("0");
                try {
                    setMealMapper.updateById(setMeal);
                } catch (Exception e) {
                    result.setCode(500);
                    result.setMessage("更新套餐数据库错误");
                    throw e;
                }
            } else if (dbSetMeal != null && dbSetMeal.getDelFlag().equals("0")) {
                result.setCode(501);
                result.setMessage("套餐已存在");
                return result;
            } else {
                try {
                    setMealMapper.insert(setMeal);
                } catch (Exception e) {
                    result.setCode(500);
                    result.setMessage("添加套餐数据库错误");
                    throw e;
                }
            }
        }
        // 数据包装并返回
        result.setCode(200);
        result.setMessage("保存成功");
        return result;
    }

    @Override
    public ApiResult removeSetMeal(Long setMealId) {
        // 删除套餐
        // 变量准备
        ApiResult result = new ApiResult();
        SetMeal dbSetMeal;
        // 数据库查询
        try {
            dbSetMeal = setMealMapper.selectById(setMealId);
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("查询套餐数据库错误");
            throw e;
        }
        if(dbSetMeal == null){
            result.setCode(502);
            result.setMessage("套餐不存在");
            return result;
        }
        // 删除套餐
        dbSetMeal.setDelFlag("1");
        try {
            setMealMapper.updateById(dbSetMeal);
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("删除套餐数据库错误");
            throw e;
        }
        // 数据包装并返回
        result.setCode(200);
        result.setMessage("删除成功");
        return result;
    }

    @Override
    public ApiResult saveSetMealDishes(SaveSetMealDishesParams params) {
        // 保存套餐中的菜品
        // 变量准备
        ApiResult result = new ApiResult();
        // 数据库操作
        // 删除该套餐中的所有菜品映射
        List<SetMealRecord> setMealRecords;
        QueryWrapper<SetMealRecord> setMealRecordQueryWrapper = new QueryWrapper<>();
        setMealRecordQueryWrapper.eq("set_meal_id",params.getSetMealId());
        setMealRecordQueryWrapper.eq("del_flag","0");
        try {
            setMealRecords = setMealRecordMapper.selectList(setMealRecordQueryWrapper);
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("查询套餐记录数据库错误");
            throw e;
        }
        // 遍历查询结果
        for (SetMealRecord setMealRecord : setMealRecords){
            UpdateWrapper<SetMealRecordDishMapping> setMealRecordDishMappingUpdateWrapper = new UpdateWrapper<>();
            setMealRecordDishMappingUpdateWrapper.eq("set_meal_record_id",setMealRecord.getSetMealRecordId());
            setMealRecordDishMappingUpdateWrapper.eq("del_flag","0");
            setMealRecordDishMappingUpdateWrapper.set("del_flag","1");
            try {
                setMealRecordDishMappingMapper.update(null,setMealRecordDishMappingUpdateWrapper);
            } catch (Exception e) {
                result.setCode(500);
                result.setMessage("删除套餐中的菜品映射数据库错误");
                throw e;
            }
        }
        // 添加早餐
        if(saveSetMealDishMapping(params.getSetMealId(), params.getBreakfastIds(), "0", result).getCode()!=200){
            return result;
        }
        // 添加午餐
        if(saveSetMealDishMapping(params.getSetMealId(), params.getLunchIds(), "1", result).getCode()!=200){
            return result;
        }
        // 添加晚餐
        if(saveSetMealDishMapping(params.getSetMealId(), params.getDinnerIds(), "2", result).getCode()!=200){
            return result;
        }
        // 数据包装并返回
        result.setCode(200);
        result.setMessage("保存成功");
        return result;
    }

    @Override
    public ApiResult saveCustomerSetMeal(SaveCustomerSetMealParams params) {
        // 保存客户套餐
        // 变量准备
        ApiResult result = new ApiResult();
        // 数据库操作
        for (Long customerId : params.getCustomerIds()){
            // 删除客户套餐映射
            UpdateWrapper<SetMealCustomerMapping> setMealCustomerMappingUpdateWrapper = new UpdateWrapper<>();
            setMealCustomerMappingUpdateWrapper.eq("customer_id",customerId);
            setMealCustomerMappingUpdateWrapper.eq("date",params.getDate());
            setMealCustomerMappingUpdateWrapper.eq("del_flag","0");
            setMealCustomerMappingUpdateWrapper.set("del_flag","1");
            try {
                setMealCustomerMappingMapper.update(null,setMealCustomerMappingUpdateWrapper);
            } catch (Exception e) {
                result.setCode(500);
                result.setMessage("删除客户套餐映射数据库错误");
                throw e;
            }
            // 添加客户套餐映射
            SetMealCustomerMapping setMealCustomerMapping = new SetMealCustomerMapping();
            setMealCustomerMapping.setCustomerId(customerId);
            setMealCustomerMapping.setSetMealId(params.getSetMealId());
            setMealCustomerMapping.setDate(params.getDate());
            try {
                setMealCustomerMappingMapper.insert(setMealCustomerMapping);
            } catch (Exception e) {
                result.setCode(500);
                result.setMessage("添加客户套餐映射数据库错误");
                throw e;
            }
        }
        result.setCode(200);
        result.setMessage("保存成功");
        return result;
    }

    @Override
    public ApiResult<List<SetMeal>> getDailyList(String date) {
        // 获取某日套餐
        // 变量准备
        ApiResult<List<SetMeal>> result = new ApiResult<>();
        List<SetMeal> data;
        DietCalendar dbDietCalendar;
        // 数据库查询
        try{
            dbDietCalendar = dietCalendarMapper.selectOne(new QueryWrapper<DietCalendar>().eq("date", date));
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("查找日历记录数据库错误");
            throw e;
        }
        if (dbDietCalendar == null) {
            result.setCode(501);
            result.setMessage("该日期暂未配置套餐");
            return result;
        }
        try {
            data = setMealMapper.selectSetMealsByDietCalendarId(dbDietCalendar.getDietCalendarId());
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("按膳食日历id查询套餐数据库错误");
            throw e;
        }
        // 数据包装并返回
        result.setCode(200);
        result.setData(data);
        result.setMessage("查询成功");
        return result;
    }

    private ApiResult saveSetMealDishMapping(Long setMealId, List<Long> dishIds, String time, ApiResult result){
        // 添加套餐中的菜品映射
        for (Long dishId : dishIds){
            SetMealRecordDishMapping setMealRecordDishMapping = new SetMealRecordDishMapping();
            SetMealRecord setMealRecord;
            QueryWrapper<SetMealRecord> setMealRecordQueryWrapper = new QueryWrapper<>();
            setMealRecordQueryWrapper.eq("set_meal_id",setMealId);
            setMealRecordQueryWrapper.eq("time",time);
            setMealRecordQueryWrapper.eq("del_flag","0");
            try {
                setMealRecord = setMealRecordMapper.selectOne(setMealRecordQueryWrapper);
            } catch (Exception e) {
                result.setCode(500);
                result.setMessage("查询套餐中的菜品映射数据库错误");
                throw e;
            }
            if(setMealRecord == null){
                SetMealRecord newSetMealRecord = new SetMealRecord();
                SetMealRecord dbSetMealRecord;
                newSetMealRecord.setSetMealId(setMealId);
                newSetMealRecord.setTime(time);
                try {
                    setMealRecordMapper.insert(newSetMealRecord);
                } catch (Exception e) {
                    result.setCode(500);
                    result.setMessage("添加套餐中的菜品映射数据库错误");
                    throw e;
                }
                try {
                    dbSetMealRecord = setMealRecordMapper.selectOne(setMealRecordQueryWrapper);
                } catch (Exception e) {
                    result.setCode(500);
                    result.setMessage("查询套餐中的菜品映射数据库错误");
                    throw e;
                }
                setMealRecordDishMapping.setSetMealRecordId(dbSetMealRecord.getSetMealRecordId());
                setMealRecordDishMapping.setDishId(dishId);
            } else {
                setMealRecordDishMapping.setSetMealRecordId(setMealRecord.getSetMealRecordId());
                setMealRecordDishMapping.setDishId(dishId);
            }
            try {
                setMealRecordDishMappingMapper.insert(setMealRecordDishMapping);
            } catch (Exception e) {
                result.setCode(500);
                result.setMessage("添加套餐中的菜品映射数据库错误");
                throw e;
            }
        }
        result.setCode(200);
        result.setMessage("保存成功");
        return result;
    }
}
