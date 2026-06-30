package com.eldercare.system.vo.caregiver;

import lombok.Data;

import java.util.List;

/**
 * 已购买护理项目列表视图
 */
@Data
public class PurchasedItemsListVO {

    private List<PurchasedItemsVO> list; // 已购买护理项目列表
    private int total; // 总记录数

    /**
     * 创建已购买护理项目列表视图
     *
     * @param list 已购买护理项目列表
     * @param total 总记录数
     */
    public PurchasedItemsListVO(List<PurchasedItemsVO> list, int total) {
        this.list = list;
        this.total = total;
    }
}
