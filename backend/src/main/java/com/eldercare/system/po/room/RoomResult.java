package com.eldercare.system.po.room;

import lombok.Data;

import java.util.List;

@Data
public class RoomResult {
    private String  label;
    private List<RoomOption> options;
    public RoomResult(String label, List<RoomOption> options){
        this.label = label;
        this.options = options;
    }
}
