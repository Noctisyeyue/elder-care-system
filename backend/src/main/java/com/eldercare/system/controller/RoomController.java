package com.eldercare.system.controller;

import com.eldercare.system.util.ApiResult;
import com.eldercare.system.vo.bed.PairVO;
import com.eldercare.system.po.room.RoomResult;
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
    public ApiResult<List<RoomResult>> options(){
        return roomService.options();
    }

    //获取指定房间的可用床位
    @Operation(summary = "获取指定房间的可用床位")

    @GetMapping("/{roomNumber}/beds")
    public ApiResult<List<PairVO>> freeBeds(@PathVariable String roomNumber){
        return bedService.selectFreeBeds(roomNumber);
    }
    //更新床位信息，将床位状态更新为空闲free
    @Operation(summary = "更新床位信息")

    @PutMapping("/{roomNumber}/bed/{bedNumber}/status")
    public ApiResult updateBedStatus(@PathVariable Long roomNumber, @PathVariable Long bedNumber){
        return roomService.updateBedStatus(roomNumber, bedNumber);
    }
}
