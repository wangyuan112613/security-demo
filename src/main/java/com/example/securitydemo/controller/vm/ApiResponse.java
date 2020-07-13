package com.example.securitydemo.controller.vm;

import lombok.Data;

/**
 * 响应报文
 *
 */
@Data
public class ApiResponse {
    private Boolean success;
    private String message;

    public ApiResponse(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
