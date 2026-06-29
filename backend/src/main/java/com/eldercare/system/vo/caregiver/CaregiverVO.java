package com.eldercare.system.vo.caregiver;

import lombok.Data;

@Data
/** Caregiver视图 */
public class CaregiverVO {
    private Long id;
    private String name;
    private String phone;
    private String gender;
    private String email;
}
