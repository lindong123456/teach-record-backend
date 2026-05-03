package com.teachrecord.service;

import com.teachrecord.domain.Teacher;
import com.teachrecord.domain.Student;
import com.teachrecord.repo.StudentRepository;
import com.teachrecord.repo.TeacherRepository;
import com.teachrecord.security.JwtService;
import com.teachrecord.web.dto.AuthDtos;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            TeacherRepository teacherRepository,
            StudentRepository studentRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public void registerTeacher(AuthDtos.RegisterRequest request) {
        if (teacherRepository.existsByUsername(request.username())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "username taken");
        }
        Teacher t = new Teacher();
        t.setUsername(request.username().trim());
        t.setDisplayName(request.displayName().trim());
        t.setPasswordHash(passwordEncoder.encode(request.password()));
        teacherRepository.save(t);
    }

    public AuthDtos.TokenResponse loginTeacher(AuthDtos.LoginRequest request) {
        Teacher t =
                teacherRepository
                        .findByUsername(request.username().trim())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid credentials"));
        if (!passwordEncoder.matches(request.password(), t.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid credentials");
        }
        String token = jwtService.createTeacherToken(t.getId(), "teacher:" + t.getId());
        return new AuthDtos.TokenResponse(token);
    }

    public AuthDtos.ParentLoginResponse loginParent(AuthDtos.ParentLoginRequest request) {
        Student s =
                studentRepository
                        .findByLoginUsername(request.loginUsername().trim())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid credentials"));
        if (!passwordEncoder.matches(request.password(), s.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid credentials");
        }
        String token =
                jwtService.createParentToken(s.getTeacherId(), s.getId(), "parent:" + s.getId());
        return new AuthDtos.ParentLoginResponse(token, s.getName());
    }

    /** 根据登录账号查询学生姓名，供登录页展示；账号不存在时 404。 */
    public AuthDtos.ParentStudentPreview previewParentStudent(String loginUsername) {
        if (loginUsername == null || loginUsername.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "loginUsername required");
        }
        Student s =
                studentRepository
                        .findByLoginUsername(loginUsername.trim())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return new AuthDtos.ParentStudentPreview(s.getName());
    }
}
