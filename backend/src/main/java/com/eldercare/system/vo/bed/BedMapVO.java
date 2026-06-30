package com.eldercare.system.vo.bed;

import com.eldercare.system.entity.Bed;
import lombok.Data;

import java.util.List;

/**
 * 床位示意图视图
 */
@Data
public class BedMapVO {

    private String roomNumber; // 房间号
    private List<Bed> beds; // 房间床位列表
    private List<BedVO> details; // 床位使用详情
}
