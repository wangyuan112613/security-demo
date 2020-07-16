package com.example.securitydemo.controller.vm;

import javax.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 登录（注册）的请求报文
 */
@Data
public class LoginRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
