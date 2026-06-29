package com.eldercare.system.po.bed.result;

import lombok.Data;

import java.util.List;


@Data
public class FreeRoomsResult {
    private String label;
    private List<Pair> options;
}
