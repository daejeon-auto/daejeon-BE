package com.pcs.daejeon.common;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Result<T> {
    private T data;
    private boolean hasError;

    public Result(T data) {
        this.data = data;
        this.hasError = false;
    }
}