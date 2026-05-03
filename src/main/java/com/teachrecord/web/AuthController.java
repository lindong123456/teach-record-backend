package com.teachrecord.web;

import com.teachrecord.service.AuthService;
import com.teachrecord.web.dto.AuthDtos;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@RequestBody @Valid AuthDtos.RegisterRequest request) {
        authService.registerTeacher(request);
    }

    @PostMapping("/login")
    public AuthDtos.TokenResponse login(@RequestBody @Valid AuthDtos.LoginRequest request) {
        return authService.loginTeacher(request);
    }

    @PostMapping("/parent/login")
    public AuthDtos.ParentLoginResponse parentLogin(@RequestBody @Valid AuthDtos.ParentLoginRequest request) {
        return authService.loginParent(request);
    }

    @GetMapping("/parent/student-preview")
    public AuthDtos.ParentStudentPreview parentStudentPreview(@RequestParam String loginUsername) {
        return authService.previewParentStudent(loginUsername);
    }
}
