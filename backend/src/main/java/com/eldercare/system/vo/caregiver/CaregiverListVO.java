package com.eldercare.system.vo.caregiver;

import lombok.Data;

import java.util.List;

@Data
/** CaregiverList视图 */
public class CaregiverListVO {
    private List<CaregiverVO> list;
    private int total;
    public CaregiverListVO(List<CaregiverVO> list, int total) {
        this.list = list;
        this.total = total;
    }
}
