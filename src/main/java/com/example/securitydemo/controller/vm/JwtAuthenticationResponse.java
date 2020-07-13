package com.example.securitydemo.controller.vm;


import lombok.Data;

/**
 *
 * 登录的响应报文body，主要包含jwt及jwt类型
 *
 */
@Data
public class JwtAuthenticationResponse {
    private String accessToken;
    private String tokenType = "Bearer";

    public JwtAuthenticationResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}