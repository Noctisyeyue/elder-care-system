package com.eldercare.system.dto.diet;

import lombok.Data;

import java.util.List;

@Data
public class RemoveDishRequest {
    private List<Long> id;
}
