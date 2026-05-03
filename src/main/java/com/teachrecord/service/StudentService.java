package com.teachrecord.service;

import com.teachrecord.domain.Student;
import com.teachrecord.repo.StudentRepository;
import com.teachrecord.school.SgGradeLevel;
import com.teachrecord.util.CredentialsGenerator;
import com.teachrecord.web.dto.StudentDtos;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final CredentialsGenerator credentialsGenerator;

    public StudentService(
            StudentRepository studentRepository,
            PasswordEncoder passwordEncoder,
            CredentialsGenerator credentialsGenerator) {
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
        this.credentialsGenerator = credentialsGenerator;
    }

    @Transactional
    public StudentDtos.StudentView create(long teacherId, StudentDtos.StudentCreate req) {
        String login;
        int guard = 0;
        do {
            login = credentialsGenerator.newLoginUsername();
            if (++guard > 20) {
                throw new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR, "failed to generate login");
            }
        } while (studentRepository.existsByLoginUsername(login));
        String plain = credentialsGenerator.newPassword(10);
        Student s = new Student();
        s.setTeacherId(teacherId);
        s.setName(req.name().trim());
        s.setLoginUsername(login);
        s.setPasswordHash(passwordEncoder.encode(plain));
        s.setParentPasswordPlain(plain);
        s.setHourlyRate(req.hourlyRate());
        s.setGradeLevel(defaultOrParseGradeLevel(req.gradeLevel()));
        studentRepository.save(s);
        return toView(s);
    }

    public List<StudentDtos.StudentView> list(long teacherId) {
        return studentRepository.findByTeacherIdOrderByCreatedAtDesc(teacherId).stream()
                .map(this::toView)
                .toList();
    }

    @Transactional
    public StudentDtos.StudentView updateRate(long teacherId, long id, java.math.BigDecimal rate) {
        Student s = studentRepository
                .findByIdAndTeacherId(id, teacherId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (rate.compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "rate must be >= 0");
        }
        s.setHourlyRate(rate);
        return toView(s);
    }

    @Transactional
    public StudentDtos.StudentView updateGradeLevel(long teacherId, long id, String rawGrade) {
        Student s = studentRepository
                .findByIdAndTeacherId(id, teacherId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        SgGradeLevel g = SgGradeLevel.fromCode(rawGrade);
        if (g == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "gradeLevel: use P1–P6, S1–S5, or JC1–JC2");
        }
        s.setGradeLevel(g.name());
        return toView(s);
    }

    @Transactional
    public StudentDtos.StudentView resetPassword(long teacherId, long id) {
        Student s = studentRepository
                .findByIdAndTeacherId(id, teacherId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        String plain = credentialsGenerator.newPassword(10);
        s.setPasswordHash(passwordEncoder.encode(plain));
        s.setParentPasswordPlain(plain);
        studentRepository.saveAndFlush(s);
        return toView(s);
    }

    private String defaultOrParseGradeLevel(String raw) {
        if (raw == null || raw.isBlank()) {
            return SgGradeLevel.P1.name();
        }
        SgGradeLevel g = SgGradeLevel.fromCode(raw);
        if (g == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "gradeLevel: use P1–P6, S1–S5, or JC1–JC2");
        }
        return g.name();
    }

    private StudentDtos.StudentView toView(Student s) {
        return new StudentDtos.StudentView(
                s.getId(),
                s.getName(),
                s.getLoginUsername(),
                s.getHourlyRate(),
                s.getGradeLevel(),
                s.getParentPasswordPlain());
    }
}
