package com.eldercare.system.po.caregiver.results;

import lombok.Data;

@Data
public class ApplicationStatus {
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
