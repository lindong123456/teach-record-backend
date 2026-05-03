package com.teachrecord.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthDtos {

    public record RegisterRequest(
            @NotBlank @Size(max = 64) String username,
            @NotBlank @Size(max = 128) String displayName,
            @NotBlank @Size(min = 6, max = 128) String password) {}

    public record LoginRequest(
            @NotBlank String username, @NotBlank @Size(max = 128) String password) {}

    public record TokenResponse(String token) {}

    public record ParentLoginRequest(
            @NotBlank String loginUsername, @NotBlank @Size(max = 128) String password) {}

    /** 家长登录链接打开后，用于展示学生姓名（不含敏感信息）。 */
    public record ParentStudentPreview(String studentName) {}

    public record ParentLoginResponse(String token, String studentName) {}
}
