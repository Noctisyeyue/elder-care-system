package com.eldercare.system.service;

import com.eldercare.system.entity.NursingRecord;
import com.eldercare.system.util.ApiResult;
import com.eldercare.system.po.nursing.params.*;
import com.eldercare.system.po.nursing.result.*;

import java.util.List;

/** 护理服务接口 */
public interface NursingService {

    /**
     * 分页查询护理级别列表
     *
     * @param params 查询参数
     * @return 护理级别列表
     */
    ApiResult<LevelListResult> getNursingLevelList(LevelListParams params);

    /**
     * 新增护理级别
     *
     * @param params 护理级别参数
     * @return 操作结果
     */
    ApiResult addLevel(LevelParams params);

    /**
     * 修改护理级别
     *
     * @param params 护理级别参数
     * @return 操作结果
     */
    ApiResult updateLevel(LevelParams params);

    /**
     * 分页查询护理项目列表
     *
     * @param params 查询参数
     * @return 护理项目列表
     */
    ApiResult<ItemListResult> getNursingItemList(ItemListParams params);

    /**
     * 查询护理级别关联的护理项目
     *
     * @param params 查询参数
     * @return 护理项目列表
     */
    ApiResult<ItemListResult> getNursingItemListByNursingLevelId(LevelItemListParams params);

    /**
     * 移除护理级别关联项目
     *
     * @param params 级别项目参数
     * @return 操作结果
     */
    ApiResult removeLevelItem(LevelItemParams params);

    /**
     * 新增护理级别关联项目
     *
     * @param params 级别项目参数
     * @return 操作结果
     */
    ApiResult addLevelItem(LevelItemParams params);

    /**
     * 删除护理项目
     *
     * @param id 护理项目ID
     * @return 操作结果
     */
    ApiResult deleteItem(Long id);

    /**
     * 新增护理项目
     *
     * @param params 护理项目新增参数
     * @return 操作结果
     */
    ApiResult addItem(ItemAddParams params);

    /**
     * 修改护理项目
     *
     * @param params 护理项目参数
     * @return 操作结果
     */
    ApiResult updateItem(ItemParams params);

    /**
     * 查询客户已购护理项目
     *
     * @param customerId 客户ID
     * @return 护理项目记录列表
     */
    ApiResult<List<ItemRecordResult>> getCustomerItems(Long customerId);

    /**
     * 为客户新增护理项目记录
     *
     * @param params 项目记录参数
     * @return 操作结果
     */
    ApiResult addCustomerItemRecords(AddItemRecordsParams params);

    /**
     * 移除客户护理项目记录
     *
     * @param params 项目记录参数
     * @return 操作结果
     */
    ApiResult removeCustomerItemRecords(AddItemRecordsParams params);

    /**
     * 查询客户护理执行记录
     *
     * @param customerId 查询参数
     * @return 护理执行记录列表
     */
    ApiResult<NursingRecordListResult> getCustomerItemRecords(NursingRecordListParams customerId);

    /**
     * 删除护理执行记录
     *
     * @param record 护理执行记录
     * @return 操作结果
     */
    ApiResult removeRecord(NursingRecord record);

    /**
     * 移除客户护理项目
     *
     * @param itemId 客户护理项目ID
     * @return 操作结果
     */
    ApiResult removeCustomerItem(Long itemId);

    /**
     * 续费客户护理项目
     *
     * @param params 续费参数
     * @return 操作结果
     */
    ApiResult renew(RenewParams params);

    /**
     * 查询客户护理级别项目列表
     *
     * @param params 查询参数
     * @return 护理项目列表
     */
    ApiResult<ItemListResult> customerLevelItemList(CustomerLevelItemListParams params);

    /**
     * 新增护理执行记录
     *
     * @param record 护理记录参数
     * @param token  登录令牌
     * @return 操作结果
     */
    ApiResult addRecord(NursingRecordParam record, String token);

    /**
     * 保存护理级别项目配置
     *
     * @param params 级别项目配置参数
     * @return 操作结果
     */
    ApiResult saveLevelItems(LevelItemsParams params);
}
