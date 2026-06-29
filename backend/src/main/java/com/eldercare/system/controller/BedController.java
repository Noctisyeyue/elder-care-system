package com.eldercare.system.controller;

import com.eldercare.system.util.ApiResult;
import com.eldercare.system.po.bed.params.SwapParams;
import com.eldercare.system.po.bed.result.*;
import com.eldercare.system.po.bed.params.ListParams;
import com.eldercare.system.service.BedService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/** 床位管理控制器 */
@Tag(name = "床位管理接口")
@RestController
@RequestMapping("/bed")
public class BedController {
    @Autowired
    private BedService bedService;


    // 参数说明
//    @Parameters({
//        @Parameter(name = "floor", description = "楼层"),
//    })
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "成功")
//    })
    @Operation(summary = "获取某一层的床位示意图信息")
    @GetMapping("/map")
    public ApiResult<List<MapResult>> map(String floor){
        return bedService.getMap(floor);
    }

    @Operation(summary = "获取正在使用床位的客户信息")
    @GetMapping("/list")
    public ApiResult<ListResult> list(ListParams params){
        return bedService.getList(params);
    }

    @Operation(summary = "修改床位使用信息")
    @PutMapping("/update/{id}")
    public ApiResult updateUsageEndDate(@PathVariable Long id, @RequestBody Map<String,String> params){
        return bedService.updateUsageEndDateById(id, params);
    }

    @Operation(summary = "获取所有有空闲床位的房间")
    @GetMapping("/freeRooms")
    public ApiResult<List<FreeRoomsResult>> freeRooms(){
        return bedService.selectFreeRooms();
    }

    @Operation(summary = "获取某个房间的所有空闲床位信息")
    @GetMapping("/freeBeds/{roomNumber}")
    public ApiResult<List<Pair>> freeBeds(@PathVariable String roomNumber){
        return bedService.selectFreeBeds(roomNumber);
    }

    @Operation(summary = "使客户进行床位调换")
    @PostMapping("/swap")
    public ApiResult swap(@RequestBody SwapParams params){
        return bedService.swap(params);
    }

    @Operation(summary = "获取楼层列表")
    @GetMapping("/floorList")
    public ApiResult<List<String>> floorList(){
        return bedService.floorList();
    }

    @Operation(summary = "获取总空闲床位数")
    @GetMapping("/freeBedCount")
    public ApiResult<Long> freeBedCount(){
        return bedService.freeBedCount();
    }

    @Operation(summary = "获取总床位数")
    @GetMapping("/bedCount")
    public ApiResult<Long> BedCount(){
        return bedService.BedCount();
    }
}
