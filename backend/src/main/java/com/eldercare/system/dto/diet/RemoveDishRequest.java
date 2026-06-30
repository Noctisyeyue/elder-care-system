package com.eldercare.system.dto.diet;

import lombok.Data;

import java.util.List;

/**
 * 菜品删除请求
 */
@Data
public class RemoveDishRequest {

    private List<Long> id; // 菜品 ID 列表
}
