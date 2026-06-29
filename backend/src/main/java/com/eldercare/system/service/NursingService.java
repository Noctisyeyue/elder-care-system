package com.eldercare.system.service;

import com.eldercare.system.entity.NursingRecord;
import com.eldercare.system.util.ApiResult;
import com.eldercare.system.po.nursing.params.*;
import com.eldercare.system.po.nursing.result.*;

import java.util.List;

/** 护理服务接口 */
public interface NursingService {
    ApiResult<LevelListResult> getNursingLevelList(LevelListParams params);

    ApiResult addLevel(LevelParams params);

    ApiResult updateLevel(LevelParams params);

    ApiResult<ItemListResult> getNursingItemList(ItemListParams params);

    ApiResult<ItemListResult> getNursingItemListByNursingLevelId(LevelItemListParams params);

    ApiResult removeLevelItem(LevelItemParams params);

    ApiResult addLevelItem(LevelItemParams params);

    ApiResult deleteItem(Long id);

    ApiResult addItem(ItemAddParams params);

    ApiResult updateItem(ItemParams params);

    ApiResult<List<ItemRecordResult>> getCustomerItems(Long customerId);

    ApiResult addCustomerItemRecords(AddItemRecordsParams params);

    ApiResult removeCustomerItemRecords(AddItemRecordsParams params);

    ApiResult<NursingRecordListResult> getCustomerItemRecords(NursingRecordListParams customerId);

    ApiResult removeRecord(NursingRecord record);

    ApiResult removeCustomerItem(Long itemId);

    ApiResult renew(RenewParams params);

    ApiResult<ItemListResult> customerLevelItemList(CustomerLevelItemListParams params);

    ApiResult addRecord(NursingRecordParam record, String token);

    ApiResult saveLevelItems(LevelItemsParams params);
}
