package com.example.SmartEcommercePlatform.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private String message;
    private T data;
    private int status;

    public ApiResponse(String message, T data) {
        this.message = message;
        this.data = data;
        this.status = 200;
    }
}