package com.eldercare.system.controller;

import com.eldercare.system.entity.NursingItemRecord;
import com.eldercare.system.entity.NursingRecord;
import com.eldercare.system.util.ApiResult;
import com.eldercare.system.dto.nursing.*;
import com.eldercare.system.vo.nursing.*;
import com.eldercare.system.service.NursingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/** 护理管理控制器 */
@Tag(name = "护理管理接口")
@RestController
@RequestMapping("/nursing")
public class NursingController {
    @Autowired
    private NursingService nursingService;

    @Operation(summary = "获取护理级别列表")
    @GetMapping("/level/list")
    public ApiResult<LevelListVO> levelList(LevelListRequest params){
        return nursingService.getNursingLevelList(params);
    }

    @Operation(summary = "添加护理级别")
    @PostMapping("/level/add")
    public ApiResult addLevel(@RequestBody LevelRequest params){
        return nursingService.addLevel(params);
    }

    @Operation(summary = "修改护理级别信息")
    @PutMapping("/level/update")
    public ApiResult updateLevel(@RequestBody LevelRequest params){
        return nursingService.updateLevel(params);
    }

    @Operation(summary = "获取某个护理级别的护理项目列表")
    @GetMapping("/level/item/list")
    public ApiResult<ItemListVO> levelItemList(LevelItemListRequest params){
        return nursingService.getNursingItemListByNursingLevelId(params);
    }

    @Operation(summary = "将某个护理项目从某个护理级别中移除")
    @PostMapping("/level/item/remove")
    public ApiResult removeLevelItem(@RequestBody LevelItemRequest params){
        return nursingService.removeLevelItem(params);
    }

    @Operation(summary = "将某个护理项目添加到某个护理级别中")
    @PostMapping("/level/item/add")
    public ApiResult addLevelItem(@RequestBody LevelItemRequest params){
        return nursingService.addLevelItem(params);
    }

    @PostMapping("/level/item/save")
    public ApiResult saveLevelItems(@RequestBody LevelItemsRequest params){
        return nursingService.saveLevelItems(params);
    }

    @Operation(summary = "获取护理项目列表")
    @GetMapping("/item/list")
    public ApiResult<ItemListVO> itemList(ItemListRequest params){
        return nursingService.getNursingItemList(params);
    }

    @Operation(summary = "删除护理项目")
    @DeleteMapping("/item/delete/{id}")
    public ApiResult deleteItem(@PathVariable Long id){
        return nursingService.deleteItem(id);
    }

    @Operation(summary = "添加护理项目")
    @PostMapping("/item/add")
    public ApiResult addItem(@RequestBody ItemAddRequest params){
        return nursingService.addItem(params);
    }

    @Operation(summary = "修改护理项目")
    @PutMapping("/item/update")
    public ApiResult updateItem(@RequestBody ItemRequest params){
        return nursingService.updateItem(params);
    }

    @Operation(summary = "获取某个客户的护理项目记录列表")
    @GetMapping("/customer/items")
    public ApiResult<List<ItemRecordVO>> getCustomerItems(Long customerId){
        return nursingService.getCustomerItems(customerId);
    }

    @Operation(summary = "添加客户的护理项目记录")
    @PostMapping("/customer/add")
    public ApiResult addCustomerItemRecords(@RequestBody AddItemRecordsRequest params){
        return nursingService.addCustomerItemRecords(params);
    }

    @Operation(summary = "删除客户的护理项目记录")
    @PostMapping("/customer/remove")
    public ApiResult removeCustomerItemRecords(@RequestBody AddItemRecordsRequest params){
        return nursingService.removeCustomerItemRecords(params);
    }

    @Operation(summary = "获取某个客户的护理级别对应的护理项目列表")
    @GetMapping("/customerLevel/item/list")
    public ApiResult<ItemListVO> customerLevelItemList(CustomerLevelItemListRequest params){
        return nursingService.customerLevelItemList(params);
    }

    @Operation(summary = "获取某个客户的护理记录")
    @GetMapping("/record/list")
    public ApiResult<NursingRecordListVO> getCustomerItemRecords(NursingRecordListRequest params){
        return nursingService.getCustomerItemRecords(params);
    }

    @Operation(summary = "获取护理级别列表")
    @PostMapping("/record/remove")
    public ApiResult removeRecord(@RequestBody NursingRecord record){
        return nursingService.removeRecord(record);
    }

    @Operation(summary = "删除某个客户的护理记录")
    @DeleteMapping("/remove/{id}")
    public ApiResult removeCustomerItem(@PathVariable long id){
        return nursingService.removeCustomerItem(id);
    }

    @Operation(summary = "修改某个客户的护理记录")
    @PostMapping("/renew")
    public ApiResult renew(@RequestBody RenewRequest params){
        return nursingService.renew(params);
    }

    // 添加护理记录
    @Operation(summary = "添加某个客户的护理记录")
    @PostMapping("/record/add")
    public ApiResult addRecord(@RequestBody NursingRecordRequest record,@RequestHeader(value = "Authorization", required = false) String token){
        return nursingService.addRecord(record,token);
    }
}
