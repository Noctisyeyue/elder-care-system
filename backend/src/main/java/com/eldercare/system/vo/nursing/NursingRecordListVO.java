package com.eldercare.system.vo.nursing;

import lombok.Data;

import java.util.List;

/**
 * 护理记录列表视图
 */
@Data
public class NursingRecordListVO {

    private List<RecordVO> list; // 护理记录列表
    private Integer total; // 总记录数
}
