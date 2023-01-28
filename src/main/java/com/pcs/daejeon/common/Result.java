package com.pcs.daejeon.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> {
    private T data;
    private boolean hasError;

    public Result(T data) {
        this.data = data;
        this.hasError = false;
    }
}