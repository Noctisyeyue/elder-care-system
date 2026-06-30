package com.eldercare.system.controller;

import com.eldercare.system.util.ApiResult;
import com.eldercare.system.vo.bed.PairVO;
import com.eldercare.system.vo.room.RoomResultVO;
import com.eldercare.system.service.BedService;
import com.eldercare.system.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** 房间管理控制器 */
@Tag(name = "房间管理接口")
@RestController
@RequestMapping("/room")
public class RoomController {
    @Autowired
    private RoomService roomService;
    @Autowired
    private BedService bedService;
    //  获取房间所有房间
    @Operation(summary = "获取房间所有房间")
    @GetMapping("/options")
    public ApiResult<List<RoomResultVO>> options(@RequestParam String building){
        return roomService.options(building);
    }

    //获取指定房间的可用床位
    @Operation(summary = "获取指定房间的可用床位")

    @GetMapping("/{roomNumber}/beds")
    public ApiResult<List<PairVO>> freeBeds(@PathVariable String roomNumber, @RequestParam String building){
        return bedService.selectFreeBeds(roomNumber, building);
    }
    //更新床位信息，将床位状态更新为空闲free
    @Operation(summary = "更新床位信息")

    @PutMapping("/{roomNumber}/bed/{bedNumber}/status")
    public ApiResult updateBedStatus(@RequestParam String building, @PathVariable Long roomNumber, @PathVariable Long bedNumber){
        return roomService.updateBedStatus(building, roomNumber, bedNumber);
    }
}
