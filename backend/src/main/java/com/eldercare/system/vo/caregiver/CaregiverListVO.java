package com.eldercare.system.vo.caregiver;

import lombok.Data;

import java.util.List;

/**
 * 健康管家列表视图
 */
@Data
public class CaregiverListVO {

    private List<CaregiverVO> list; // 健康管家列表
    private int total; // 总记录数

    /**
     * 创建健康管家列表视图
     *
     * @param list 健康管家列表
     * @param total 总记录数
     */
    public CaregiverListVO(List<CaregiverVO> list, int total) {
        this.list = list;
        this.total = total;
    }
}
