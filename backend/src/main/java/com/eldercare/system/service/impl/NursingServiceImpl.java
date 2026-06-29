package com.eldercare.system.service.impl;
import com.eldercare.system.service.NursingService;

import com.auth0.jwt.interfaces.Claim;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eldercare.system.entity.*;
import com.eldercare.system.mapper.*;
import com.eldercare.system.util.ApiResult;
import com.eldercare.system.po.nursing.params.*;
import com.eldercare.system.po.nursing.result.*;
import com.eldercare.system.util.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
/**
 * 护理管理业务实现。
 */
public class NursingServiceImpl implements NursingService{
    /** 护理级别数据访问对象。 */
    @Autowired
    private NursingLevelMapper nursingLevelMapper;
    /** 护理项目数据访问对象。 */
    @Autowired
    private NursingItemMapper nursingItemMapper;
    /** 护理级别项目关系数据访问对象。 */
    @Autowired
    private NursingLevelItemMappingMapper nursingLevelItemMappingMapper;
    /** 客户护理项目记录数据访问对象。 */
    @Autowired
    private NursingItemRecordMapper nursingItemRecordMapper;
    /** 护理执行记录数据访问对象。 */
    @Autowired
    private NursingRecordMapper nursingRecordMapper;
    /** 客户数据访问对象。 */
    @Autowired
    private CustomerMapper customerMapper;
    /** 用户数据访问对象。 */
    @Autowired
    private UserMapper userMapper;


    /**
     * 分页查询护理级别列表。
     *
     * @param params 护理级别查询参数
     * @return 护理级别分页列表
     */
    @Override
    public ApiResult<LevelListResult> getNursingLevelList(LevelListParams params) {
        // 获取项目级别列表
        // 变量准备
        ApiResult<LevelListResult> result = new ApiResult<>();
        LevelListResult data = new LevelListResult();
        List<LevelResult> levelResults = new ArrayList<>();
        List<NursingLevel> nursingLevels;
        Long total;
        IPage<NursingLevel> page = new Page<>(params.getPageNum(), params.getPageSize());
        // 数据库查询
        String queryStatus = switch (params.getStatus()){
          case "启用" -> "0";
          case "停用" -> "1";
          default -> "9";
        };
        QueryWrapper<NursingLevel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", queryStatus);
        queryWrapper.eq("del_flag", "0");
        queryWrapper.orderByDesc("nursing_level_id");
        // 获取项目级别列表
        try {
            nursingLevels = nursingLevelMapper.selectList(page, queryWrapper);
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("查找项目级别数据库错误");
            throw e;
        }
        // 获取项目级别总数
        try {
            total = nursingLevelMapper.selectCount(queryWrapper);
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("查找项目级别数据库错误");
            throw e;
        }
        // 将数据包装成前端所需数据格式
        for(NursingLevel nursingLevel : nursingLevels){
            LevelResult levelResult = new LevelResult();
            levelResult.setId(nursingLevel.getNursingLevelId());
            levelResult.setLevel(nursingLevel.getNursingLevelName());
            levelResult.setStatus(params.getStatus());
            levelResults.add(levelResult);
        }
        // 数据包装并返回
        data.setRecords(levelResults);
        data.setTotal(total);
        result.setCode(200);
        result.setData(data);
        result.setMessage("查询成功");
        return result;
    }

    /**
     * 新增护理级别。
     *
     * @param params 护理级别保存参数
     * @return 新增处理结果
     */
    @Override
    public ApiResult addLevel(LevelParams params) {
        // 添加项目级别
        // 变量准备
        ApiResult result = new ApiResult();
        NursingLevel nursingLevel = new NursingLevel(), dbNursingLevel;
        nursingLevel.setNursingLevelName(params.getLevel());
        String status = switch (params.getStatus()){
            case "启用" -> "0";
            case "停用" -> "1";
            default -> "9";
        };
        nursingLevel.setStatus(status);
        // 数据库操作
        // 查询项目级别
        QueryWrapper<NursingLevel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("nursing_level_name", params.getLevel());
        try {
            dbNursingLevel = nursingLevelMapper.selectOne(queryWrapper);
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("查询项目级别数据库错误");
            throw e;
        }
        if (dbNursingLevel != null){
            result.setCode(501);
            result.setMessage("项目级别已存在");
            return result;
        }
        // 添加项目级别
        try {
            nursingLevelMapper.insert(nursingLevel);
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("添加项目级别数据库错误");
            throw e;
        }
        // 数据包装并返回
        result.setCode(200);
        result.setMessage("添加成功");
        return result;
    }

    /**
     * 更新护理级别。
     *
     * @param params 护理级别更新参数
     * @return 更新处理结果
     */
    @Override
    public ApiResult updateLevel(LevelParams params) {
        // 修改项目级别
        // 变量准备
        ApiResult result = new ApiResult();
        NursingLevel nursingLevel = new NursingLevel(), dbNursingLevel;
        nursingLevel.setNursingLevelId(params.getId());
        String status = switch (params.getStatus()){
            case "启用" -> "0";
            case "停用" -> "1";
            default -> "未知";
        };
        nursingLevel.setStatus(status);
        // 数据库操作
        // 查询项目级别
        try {
            dbNursingLevel = nursingLevelMapper.selectById(params.getId());
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("查询项目级别数据库错误");
            throw e;
        }
        if (dbNursingLevel == null){
            result.setCode(502);
            result.setMessage("项目级别不存在");
            return result;
        }
        // 修改项目级别
        QueryWrapper<NursingLevel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("nursing_level_id", params.getId());
        try {
            nursingLevelMapper.update(nursingLevel, queryWrapper);
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("修改项目级别数据库错误");
            throw e;
        }
        // 数据包装并返回
        result.setCode(200);
        result.setMessage("修改成功");
        return result;
    }

    /**
     * 分页查询护理项目列表。
     *
     * @param params 护理项目查询参数
     * @return 护理项目分页列表
     */
    @Override
    public ApiResult<ItemListResult> getNursingItemList(ItemListParams params) {
        // 获取项目记录列表
        // 变量准备
        ApiResult<ItemListResult> result = new ApiResult<>();
        ItemListResult data = new ItemListResult();
        List<ItemResult> list = new ArrayList<>();
        List<NursingItem> nursingItems;
        Long total;
        // 数据库查询
        String queryStatus = switch (params.getStatus()){
          case "启用" -> "0";
          case "停用" -> "1";
          default -> "9";
        };
        QueryWrapper<NursingItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", queryStatus);
        queryWrapper.like("nursing_item_name", "%"+params.getName()+"%");
        queryWrapper.orderByDesc("nursing_item_id");
        IPage<NursingItem> page = new Page<>(params.getPageNum(), params.getPageSize());
        try {
            nursingItems = nursingItemMapper.selectList(page, queryWrapper);
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("查找项目记录数据库错误");
            throw e;
        }
        total = nursingItemMapper.selectCount(queryWrapper);
        // 封装数据
        for (NursingItem nursingItem : nursingItems) {
            ItemResult itemResult = new ItemResult();
            itemResult.setId(nursingItem.getNursingItemId());
            itemResult.setCode(nursingItem.getCode());
            itemResult.setName(nursingItem.getNursingItemName());
            itemResult.setPrice(nursingItem.getPrice());
            itemResult.setFrequency(nursingItem.getExecutionCycle());
            itemResult.setCount(nursingItem.getExecutionTimes());
            itemResult.setDesc(nursingItem.getDescription());
            String status = switch (nursingItem.getStatus()){
                case "0" -> "启用";
                case "1" -> "停用";
                default -> "未知";
            };
            itemResult.setStatus(status);
            list.add(itemResult);
        }
        // 数据包装并返回
        data.setRecords(list);
        data.setTotal(total);
        result.setCode(200);
        result.setData(data);
        result.setMessage("查询成功");
        return result;
    }

    /**
     * 根据护理级别查询护理项目列表。
     *
     * @param params 护理级别项目查询参数
     * @return 护理项目分页列表
     */
    @Override
    public ApiResult<ItemListResult> getNursingItemListByNursingLevelId(LevelItemListParams params) {
        // 根据护理层级id查询护理项目
        // 变量准备
        ApiResult<ItemListResult> result = new ApiResult<>();
        ItemListResult data = new ItemListResult();
        List<ItemResult> list = new ArrayList<>();
        List<NursingItem> dbNursingItems;
        Long total;
        Integer pageStart = (params.getPageNum() - 1) * params.getPageSize();
        // 数据库查询
        try {
            dbNursingItems = nursingItemMapper.selectItemsByLevelId(params.getLevelId(), pageStart, params.getPageSize());
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("查找护理项目数据库错误");
            throw e;
        }
        try {
            total = nursingItemMapper.selectCountByLevelId(params.getLevelId());
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("查找护理项目数据库错误");
            throw e;
        }
        // 将数据包装成前端所需数据格式
        for (NursingItem nursingItem : dbNursingItems) {
            ItemResult itemResult = new ItemResult();
            itemResult.setId(nursingItem.getNursingItemId());
            itemResult.setCode(nursingItem.getCode());
            itemResult.setName(nursingItem.getNursingItemName());
            itemResult.setPrice(nursingItem.getPrice());
            itemResult.setFrequency(nursingItem.getExecutionCycle());
            itemResult.setCount(nursingItem.getExecutionTimes());
            itemResult.setStatus(nursingItem.getStatus());
            list.add(itemResult);
        }
        // 数据包装并返回
        data.setRecords(list);
        data.setTotal(total);
        result.setCode(200);
        result.setData(data);
        result.setMessage("查询成功");
        return result;
    }

    /**
     * 移除护理级别中的护理项目。
     *
     * @param params 护理级别项目关系参数
     * @return 移除处理结果
     */
    @Override
    public ApiResult removeLevelItem(LevelItemParams params) {
        // 删除护理级别中的护理项目
        // 变量准备
        ApiResult result = new ApiResult();
        List<NursingLevelItemMapping> dbNursingLevelItemMappings;
        NursingLevelItemMapping nursingLevelItemMapping = new NursingLevelItemMapping();
        nursingLevelItemMapping.setNursingItemId(params.getItemId());
        nursingLevelItemMapping.setNursingLevelId(params.getLevelId());
        String delFlag = "1";
        // 数据库查询
        QueryWrapper<NursingLevelItemMapping> qw = new QueryWrapper<>();
        qw.eq("nursing_item_id", params.getItemId());
        qw.eq("nursing_level_id", params.getLevelId());
        try {
            dbNursingLevelItemMappings = nursingLevelItemMappingMapper.selectList(qw);
        } catch (Exception e){
            result.setCode(500);
            result.setMessage("按护理级别id和护理项目id查找映射数据库错误");
            throw e;
        }
        if (dbNursingLevelItemMappings.isEmpty()){
            result.setCode(502);
            result.setMessage("此护理级别和护理项目不存在映射关系");
            return result;
        }
        for (NursingLevelItemMapping dbNursingLevelItemMapping : dbNursingLevelItemMappings) {
            if(dbNursingLevelItemMapping.getDelFlag().equals("0")){
                delFlag = dbNursingLevelItemMapping.getDelFlag();
                break;
            }
        }
        if (!delFlag.equals("0")){
            result.setCode(502);
            result.setMessage("此护理级别和护理项目不存在映射关系");
            return result;
        }
        // 数据库修改
        try {
            nursingLevelItemMappingMapper.removeMapperByItemIdAndLevelId(nursingLevelItemMapping);
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("删除护理级别中的护理项目数据库错误");
            throw e;
        }
        // 数据包装并返回
        result.setCode(200);
        result.setMessage("删除成功");
        return result;
    }

    /**
     * 添加护理级别中的护理项目。
     *
     * @param params 护理级别项目关系参数
     * @return 添加处理结果
     */
    @Override
    public ApiResult addLevelItem(LevelItemParams params) {
        // 添加护理级别中的护理项目
        // 变量准备
        ApiResult result = new ApiResult();
        List<NursingLevelItemMapping> dbNursingLevelItemMappings;
        NursingLevelItemMapping nursingLevelItemMapping;
        // 数据库查询
        QueryWrapper<NursingLevelItemMapping> qw = new QueryWrapper<>();
        qw.eq("nursing_item_id", params.getItemId());
        qw.eq("nursing_level_id", params.getLevelId());
        qw.eq("del_flag", "0");
        try {
            dbNursingLevelItemMappings = nursingLevelItemMappingMapper.selectList(qw);
        } catch (Exception e){
            result.setCode(500);
            result.setMessage("按护理级别id和护理项目id查找映射数据库错误");
            throw e;
        }
        // 数据库操作
        if (dbNursingLevelItemMappings.isEmpty()){
            nursingLevelItemMapping = new NursingLevelItemMapping();
            nursingLevelItemMapping.setNursingLevelId(params.getLevelId());
            nursingLevelItemMapping.setNursingItemId(params.getItemId());
            try {
                nursingLevelItemMappingMapper.insert(nursingLevelItemMapping);
            } catch (Exception e) {
                result.setCode(500);
                result.setMessage("删除护理级别中的护理项目数据库错误");
                throw e;
            }
        } else {
            result.setCode(502);
            result.setMessage("此护理级别和护理项目已存在映射关系");
            return result;
        }
        // 数据包装并返回
        result.setCode(200);
        result.setMessage("添加成功");
        return result;
    }

    /**
     * 删除护理项目。
     *
     * @param id 护理项目ID
     * @return 删除处理结果
     */
    @Override
    public ApiResult deleteItem(Long id) {
        // 删除护理项目
        // 变量准备
        ApiResult result = new ApiResult();
        // 数据库操作
        // 删除护理级别与项目映射
        try {
            nursingLevelItemMappingMapper.removeByItemId(id);
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("删除护理级别中的护理项目数据库错误");
            throw e;
        }
        // 删除护理项目
        try {
            nursingItemMapper.removeByItemId(id);
        } catch (Exception e){
            result.setCode(500);
            result.setMessage("删除护理项目数据库错误");
            throw e;
        }
        // 数据包装并返回
        result.setCode(200);
        result.setMessage("删除成功");
        return result;
    }

    /**
     * 新增护理项目。
     *
     * @param params 护理项目新增参数
     * @return 新增处理结果
     */
    @Override
    public ApiResult addItem(ItemAddParams params) {
        // 添加护理项目
        // 变量准备
        ApiResult result = new ApiResult();
        NursingItem nursingItem = new NursingItem();
        nursingItem.setCode(params.getCode());
        nursingItem.setNursingItemName(params.getName());
        nursingItem.setPrice(params.getPrice());
        nursingItem.setExecutionCycle(params.getFrequency());
        nursingItem.setExecutionTimes(params.getCount());
        String status = switch (params.getStatus()){
            case "启用" -> "0";
            case "停用" -> "1";
            default -> "9";
        };
        nursingItem.setStatus(status);
        nursingItem.setDescription(params.getDesc());
        // 数据库操作
        try {
            nursingItemMapper.insert(nursingItem);
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("添加护理项目数据库错误");
            throw e;
        }
        // 数据包装并返回
        result.setCode(200);
        result.setMessage("添加成功");
        return result;
    }

    /**
     * 更新护理项目。
     *
     * @param params 护理项目更新参数
     * @return 更新处理结果
     */
    @Override
    public ApiResult updateItem(ItemParams params) {
        // 修改护理项目
        // 变量准备
        ApiResult result = new ApiResult();
        NursingItem nursingItem = new NursingItem();
        nursingItem.setNursingItemId(params.getId());
        nursingItem.setCode(params.getCode());
        nursingItem.setNursingItemName(params.getName());
        nursingItem.setPrice(params.getPrice());
        nursingItem.setExecutionCycle(params.getFrequency());
        nursingItem.setExecutionTimes(params.getCount());
        String status = switch (params.getStatus()){
            case "启用" -> "0";
            case "停用" -> "1";
            default -> "9";
        };
        nursingItem.setStatus(status);
        nursingItem.setDescription(params.getDesc());
        // 数据库操作
        if(status.equals("1")){
            try {
                nursingLevelItemMappingMapper.removeByNursingItemId(params.getId());
            } catch (Exception e) {
                result.setCode(500);
                result.setMessage("更新失败，数据库错误");
                throw e;
            }
        }
        try {
            nursingItemMapper.updateById(nursingItem);
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("更新膳食项目数据库错误");
            throw e;
        }
        // 数据包装并返回
        result.setCode(200);
        result.setMessage("更新成功");
        return result;
    }

    /**
     * 查询客户已购买的护理项目。
     *
     * @param customerId 客户ID
     * @return 客户护理项目列表
     */
    @Override
    public ApiResult<List<ItemRecordResult>> getCustomerItems(Long customerId) {
        // 获取客户的护理项目
        // 变量准备
        ApiResult<List<ItemRecordResult>> result = new ApiResult<>();
        List<ItemRecordResult> data = new ArrayList<>();
        List<NursingItemRecord> dbItems;
        // 数据库查询
        try{
            dbItems = nursingItemRecordMapper.selectByCustomerId(customerId);
        }catch (Exception e){
            result.setCode(500);
            result.setMessage("按客户id查找护理项目记录数据库错误");
            throw e;
        }
        // 将数据包装成前端所需数据格式
        for (NursingItemRecord itemRecord : dbItems) {
            ItemRecordResult itemRecordResult = new ItemRecordResult();
            itemRecordResult.setId(itemRecord.getNursingItemRecordId());
            itemRecordResult.setCode(itemRecord.getNursingItemCode());
            itemRecordResult.setName(itemRecord.getNursingItemName());
            itemRecordResult.setPrice(itemRecord.getPrice());
            itemRecordResult.setFrequency(itemRecord.getExecutionCycle());
            itemRecordResult.setCount(itemRecord.getExecutedTimes());
            itemRecordResult.setBuyDate(itemRecord.getPurchasingDate());
            itemRecordResult.setBuyCount(itemRecord.getPurchasingTimes());
            itemRecordResult.setExpireDate(itemRecord.getExpirationDate());
            data.add(itemRecordResult);
        }
        // 数据包装并返回
        result.setCode(200);
        result.setData(data);
        result.setMessage("查询成功");
        return result;
    }

    /**
     * 添加客户护理项目记录。
     *
     * @param params 客户护理项目记录参数
     * @return 添加处理结果
     */
    @Override
    public ApiResult addCustomerItemRecords(AddItemRecordsParams params) {
        // 添加客户护理项目记录
        // 变量准备
        ApiResult result = new ApiResult();
        Customer customer;
        // 数据库查询
        // 获取客户信息
        try {
            customer = customerMapper.selectById(params.getCustomerId());
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("根据id获取客户数据库错误");
            throw e;
        }
        if (customer == null) {
            result.setCode(502);
            result.setMessage("客户不存在");
            return result;
        }
        // 删除护理项目记录
        removeCustomerItemRecords(params);
        // 修改客户信息
        customer.setNursingLevelId(params.getLevelId());
        customer.setCustomerType("1");
        try {
            customerMapper.updateById(customer);
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("更新客户数据库错误");
            throw e;
        }
        // 获取护理项目信息并添加护理项目记录
        for (ItemRecordAddParams itemRecordAddParam : params.getItems()){
            NursingItem nursingItem;
            NursingItemRecord nursingItemRecord = new NursingItemRecord();
            // 获取护理项目信息
            try {
                nursingItem = nursingItemMapper.selectById(itemRecordAddParam.getItemId());
            } catch (Exception e) {
                result.setCode(500);
                result.setMessage("获取护理项目数据库错误");
                throw e;
            }
            if (nursingItem == null) {
                result.setCode(502);
                result.setMessage("查询失败，该护理项目不存在");
                return result;
            }
            nursingItemRecord.setCustomerId(params.getCustomerId());
            nursingItemRecord.setNursingItemId(itemRecordAddParam.getItemId());
            nursingItemRecord.setNursingItemName(nursingItem.getNursingItemName());
            nursingItemRecord.setNursingItemCode(nursingItem.getCode());
            nursingItemRecord.setPrice(nursingItem.getPrice());
            nursingItemRecord.setExecutionCycle(nursingItem.getExecutionCycle());
            nursingItemRecord.setExecutionTimes(nursingItem.getExecutionTimes()*itemRecordAddParam.getBuyCount());
            nursingItemRecord.setExecutedTimes(0);
            nursingItemRecord.setPurchasingDate(itemRecordAddParam.getBuyDate());
            nursingItemRecord.setPurchasingTimes(itemRecordAddParam.getBuyCount());
            nursingItemRecord.setExpirationDate(itemRecordAddParam.getExpireDate());
            // 添加护理项目记录
            try {
                nursingItemRecordMapper.insert(nursingItemRecord);
            } catch (Exception e) {
                result.setCode(500);
                result.setMessage("添加护理项目记录数据库错误");
                throw e;
            }
        }
        // 数据包装并返回
        result.setCode(200);
        result.setMessage("添加成功");
        return result;
    }

    /**
     * 移除客户护理项目记录。
     *
     * @param params 客户护理项目记录参数
     * @return 移除处理结果
     */
    @Override
    public ApiResult removeCustomerItemRecords(AddItemRecordsParams params) {
        // 删除护理项目记录
        // 变量准备
        ApiResult result = new ApiResult();
        Customer customer;
        // 数据库查询
        try {
            customer = customerMapper.selectById(params.getCustomerId());
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("查询客户数据库错误");
            throw e;
        }
        // 数据库操作
        customer.setNursingLevelId(null);
        customer.setCustomerType("0");
        try {
            customerMapper.updateNursingLevelById(customer);
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("更新客户数据库错误");
            throw e;
        }
        // 数据库查询
        try {
            nursingItemRecordMapper.removeByCustomerId(params.getCustomerId());
        } catch (Exception e) {
            // 数据库错误
            result.setCode(500);
            result.setMessage("删除护理项目记录数据库错误");
            throw e;
        }
        // 数据包装并返回
        result.setCode(200);
        result.setMessage("删除成功");
        return result;
    }

    /**
     * 分页查询客户护理执行记录。
     *
     * @param params 护理记录查询参数
     * @return 护理记录分页列表
     */
    @Override
    public ApiResult<NursingRecordListResult> getCustomerItemRecords(NursingRecordListParams params) {
        // 根据客户id查询护理记录
        // 变量准备
        ApiResult<NursingRecordListResult> result = new ApiResult<>();
        NursingRecordListResult data = new NursingRecordListResult();
        List<RecordResult> recordList = new ArrayList<>();
        List<NursingRecord> dbNursingRecords;
        Integer total;
        Integer pageStart = (params.getPageNum()-1)*params.getPageSize();
        // 数据库查询
        // 获取总记录数
        try {
            total = nursingRecordMapper.countByCustomerId(params.getCustomerId());
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("通过客户id获取护理记录总数据库错误");
            throw e;
        }
        // 获取护理记录信息
        try {
            dbNursingRecords = nursingRecordMapper.selectByCustomerId(params.getCustomerId(), pageStart, params.getPageSize());
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("通过客户id获取护理记录信息数据库错误");
            throw e;
        }
        // 将数据包装成前端所需数据格式
        for (NursingRecord nursingRecord : dbNursingRecords){
            RecordResult recordResult = new RecordResult();
            User staff;
            // 获取员工信息
            try {
                staff = userMapper.selectById(nursingRecord.getUserId());
            } catch (Exception e) {
                result.setCode(500);
                result.setMessage("通过id查找用户数据库错误");
                throw e;
            }
            recordResult.setId(nursingRecord.getNursingRecordId());
            recordResult.setCode(nursingRecord.getNursingItemCode());
            recordResult.setName(nursingRecord.getNursingItemName());
            recordResult.setTimes(nursingRecord.getNursingTimes());
            recordResult.setNursingStaff(staff.getUserName());
            recordResult.setStaffPhone(staff.getPhone());
            recordResult.setNursingTime(nursingRecord.getNursingDate());
            recordList.add(recordResult);
        }
        // 数据包装并返回
        data.setList(recordList);
        data.setTotal(total);
        result.setCode(200);
        result.setData(data);
        result.setMessage("查询成功");
        return result;
    }

    /**
     * 删除护理执行记录。
     *
     * @param record 护理执行记录
     * @return 删除处理结果
     */
    @Override
    public ApiResult removeRecord(NursingRecord record) {
        // 删除护理记录
        // 变量准备
        ApiResult result = new ApiResult();
        // 数据库操作
        try {
            nursingRecordMapper.removeById(record.getNursingRecordId());
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("删除护理记录数据库错误");
            throw e;
        }
        // 数据包装并返回
        result.setCode(200);
        result.setMessage("删除成功");
        return result;
    }

    /**
     * 删除客户护理项目。
     *
     * @param itemId 客户护理项目记录ID
     * @return 删除处理结果
     */
    @Override
    public ApiResult removeCustomerItem(Long itemId) {
        // 删除护理项目
        // 变量准备
        ApiResult result = new ApiResult();
        // 删除护理项目记录
        try {
            nursingItemRecordMapper.removeById(itemId);
            result.setCode(200);
            result.setMessage("删除成功");
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("删除护理项目记录数据库错误");
            throw e;
        }
        return result;
    }

    /**
     * 续费客户护理项目。
     *
     * @param params 护理项目续费参数
     * @return 续费处理结果
     */
    @Override
    public ApiResult renew(RenewParams params) {
        ApiResult result = new ApiResult();
        //根据客户ID护理项目ID确定record,修改到期时间与购买次数
        try {
            nursingItemMapper.renew(params);
            result.setCode(200);
            result.setMessage("修改成功");
        }catch (Exception e){
            result.setCode(500);
            result.setMessage("修改数据库错误");
            throw e;
        }
        return result;
    }

    /**
     * 添加护理执行记录。
     *
     * @param record 护理执行记录参数
     * @param token 当前登录用户令牌
     * @return 添加处理结果
     */
    @Override
    public ApiResult addRecord(NursingRecordParam record, String token) {
        ApiResult result = new ApiResult();
        //从token中获取用户id
        NursingRecord nursingRecord = new NursingRecord();
        nursingRecord.setCustomerId(record.getCustomerId());
        //从token中获取当前健康管家的 id
        //根据Token获取用户名，根据用户名找到用户ID
        String username = "";
        try {
            if (token != null && token.startsWith("Bearer "))
                token = token.substring(7);
            Map<String, Claim> claims = JWTUtil.getPayloadFromToken(token);
            Claim usernameClaim = claims.get("userName");
            if (usernameClaim == null) {
                // 处理 username 不存在的情况
                result.setCode(401);
                result.setMessage("用户名不存在");
                return result;
            }
            username = usernameClaim.asString();
            // 继续业务逻辑
        } catch (Exception e) {
            result.setCode(401);
            result.setMessage("Token解析失败");
            throw e;
        }
        Long userId = userMapper.selectIdByUsername(username);
        nursingRecord.setUserId(userId);
        nursingRecord.setCustomerId(record.getCustomerId());
        nursingRecord.setNursingItemId(record.getItemId());
        nursingRecord.setNursingItemCode(record.getCode());
        nursingRecord.setNursingItemName(record.getName());
        nursingRecord.setNursingTimes(record.getTimes());
        nursingRecord.setNursingDate(record.getNursingTime());
        try {
            nursingRecordMapper.insert(nursingRecord);
            //根据客户id和护理项目id在nursing_item_record表中查询一条数据
            //修改这条数据的已执行次数，在原次数上加record.getTimes()
            nursingItemRecordMapper.updateTimes(record.getCustomerId(), record.getName(), record.getTimes());
            result.setCode(200);
            result.setMessage("添加成功");

        }catch (Exception e){
            result.setCode(500);
            result.setMessage("插入护理记录数据库错误");
            throw e;
        }

        return result;
    }

    /**
     * 保存护理级别包含的护理项目。
     *
     * @param params 护理级别项目保存参数
     * @return 保存处理结果
     */
    @Override
    public ApiResult saveLevelItems(LevelItemsParams params) {
        // 添加护理级别中的护理项目
        // 变量准备
        ApiResult result = new ApiResult();
        // 数据库操作
        // 删除已有的映射
        try {
            nursingLevelItemMappingMapper.removeByLevelId(params.getLevelId());
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("删除护理级别中的护理项目数据库错误");
            throw e;
        }
        for (Long itemId : params.getItemIds()) {
            List<NursingLevelItemMapping> dbNursingLevelItemMappings;
            NursingLevelItemMapping nursingLevelItemMapping;
            // 数据库查询
            QueryWrapper<NursingLevelItemMapping> qw = new QueryWrapper<>();
            qw.eq("nursing_item_id", itemId);
            qw.eq("nursing_level_id", params.getLevelId());
            qw.eq("del_flag", "0");
            try {
                dbNursingLevelItemMappings = nursingLevelItemMappingMapper.selectList(qw);
            } catch (Exception e) {
                result.setCode(500);
                result.setMessage("按护理级别id和护理项目id查找映射数据库错误");
                throw e;
            }
            // 数据库操作
            if (dbNursingLevelItemMappings.isEmpty()) {
                nursingLevelItemMapping = new NursingLevelItemMapping();
                nursingLevelItemMapping.setNursingLevelId(params.getLevelId());
                nursingLevelItemMapping.setNursingItemId(itemId);
                try {
                    nursingLevelItemMappingMapper.insert(nursingLevelItemMapping);
                } catch (Exception e) {
                    result.setCode(500);
                    result.setMessage("添加护理级别中的护理项目数据库错误");
                    throw e;
                }
            } else {
                result.setCode(502);
                result.setMessage("此护理级别和护理项目已存在映射关系");
                return result;
            }
        }
        // 数据包装并返回
        result.setCode(200);
        result.setMessage("添加成功");
        return result;
    }

    /**
     * 分页查询客户护理级别下的护理项目。
     *
     * @param params 客户护理级别项目查询参数
     * @return 护理项目分页列表
     */
    @Override
    public ApiResult<ItemListResult> customerLevelItemList(CustomerLevelItemListParams params) {
        // 获取客户护理级别的护理项目列表
        // 变量准备
        ApiResult<ItemListResult> result = new ApiResult<>();
        ItemListResult data = new ItemListResult();
        List<ItemResult> list = new ArrayList<>();
        List<NursingItem> dbNursingItems;
        Long total;
        Integer pageStart = (params.getPageNum()-1)*params.getPageSize();
        // 数据库查询
        // 获取总记录数
        try {
            total = nursingItemMapper.selectCountByCustomerId(params.getCustomerId(), "0");
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("通过客户id获取护理项目列表总数据库错误");
            throw e;
        }
        // 获取护理项目列表
        try {
            dbNursingItems = nursingItemMapper.selectByCustomerId(params.getCustomerId(), params.getName(), pageStart, params.getPageSize(), "0");
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("通过客户id获取护理项目列表数据库错误");
            throw e;
        }
        // 将数据包装成前端所需数据格式
        for (NursingItem nursingItem : dbNursingItems){
            ItemResult itemResult = new ItemResult();
            itemResult.setId(nursingItem.getNursingItemId());
            itemResult.setCode(nursingItem.getCode());
            itemResult.setName(nursingItem.getNursingItemName());
            itemResult.setPrice(nursingItem.getPrice());
            itemResult.setFrequency(nursingItem.getExecutionCycle());
            itemResult.setCount(nursingItem.getExecutionTimes());
            itemResult.setDesc(nursingItem.getDescription());
            itemResult.setStatus(nursingItem.getStatus());
            list.add(itemResult);
        }
        // 数据包装并返回
        data.setRecords(list);
        data.setTotal(total);
        result.setCode(200);
        result.setData(data);
        result.setMessage("查询成功");
        return result;
    }
}
