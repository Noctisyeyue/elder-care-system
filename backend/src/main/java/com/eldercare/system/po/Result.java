package com.eldercare.system.po;

import lombok.Data;

@Data
public class Result<T> {
    private int code;
    private T data;
    private String message;

}
