package com.eldercare.system.vo.caregiver;

import lombok.Data;

@Data
/** ApplicationStatus视图 */
public class ApplicationStatusVO {
    /*
    "approved":80,
    "rejected": 15,
    "submitted": 25,
    "cancelled":5
    */
    private Long approved;
    private Long rejected;
    private Long submitted;
    private Long cancelled;
}
