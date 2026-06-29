package com.eldercare.system.vo.nursing;

import lombok.Data;
import java.util.List;

/**
 * 护理项目列表视图
 */
@Data
public class ItemListVO {
    /*
    "records"(ItemVO)
    "total": 2
    */
    private List<ItemVO> records;
    private Long total;
}
