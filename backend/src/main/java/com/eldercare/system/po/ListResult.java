package com.eldercare.system.po;

import lombok.Data;

import java.util.List;

@Data
public class ListResult<T> {
    private Long total;
    private List<T> records;
}
