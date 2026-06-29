package com.eldercare.system.vo.caregiver;

import lombok.Data;

import java.util.List;

@Data
/** Home视图 */
public class HomeVO {
    /*
    返回示例
    {
        "userName":"张三",
        "dailyCarecount": 10,
        "compareCarecount": 35,
        "totalcarecount":205216,
        "completedcarecount":150000,
        "uncompletedcarecount":55216,
        "totalCaredPeople":55231,
        "counts":[50，25，70，35，20，45，60，55，80，65，75，90],
        outingApplicationStatus":{
            "approved": 120,
            "rejected": 30,
            "submitted": 58,
            "cancelled": 10
        },
        checkoutApplicationStatus":{
            "approved":80,
            "rejected": 15,
            "submitted": 25,
            "cancelled":5
        }
    }
    */
    private String userName;
    private Long dailyCareCount;
    private Long compareCareCount;
    private Long totalCareCount;
    private Long completedCareCount;
    private Long uncompletedCareCount;
    private Long totalCaredPeople;
    private List<Long> counts;
    private ApplicationStatusVO outingApplicationStatus;
    private ApplicationStatusVO checkoutApplicationStatus;
}
