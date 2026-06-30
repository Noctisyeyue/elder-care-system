package com.eldercare.system.vo.bed;

import lombok.Data;

import java.util.List;

/**
 * 床位列表视图
 */
@Data
public class BedListVO {

    private List<BedVO> list; // 床位信息列表
    private Integer total; // 总记录数
}
