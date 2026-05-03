package com.teachrecord.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(
        name = "students",
        indexes = {
                @Index(columnList = "teacher_id"),
                @Index(columnList = "login_username", unique = true)
        })
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "teacher_id", nullable = false)
    private Long teacherId;

    @Column(nullable = false, length = 128)
    private String name;

    /** Unique login id for parent portal (e.g. shared link + this account). */
    @Column(name = "login_username", nullable = false, unique = true, length = 64)
    private String loginUsername;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    /**
     * 家长密码明文副本，仅供教师端列表展示；校验仍以 password_hash 为准。勿用于对外公开接口。
     */
    @Column(name = "parent_password_plain", length = 64)
    private String parentPasswordPlain;

    /** Hourly rate in currency units; lesson rows snapshot this into unit_price. */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal hourlyRate = BigDecimal.ZERO;

    /**
     * Singapore school level code (P1…P6, S1…S5, JC1, JC2). Set at creation; updatable in teacher
     * app.
     */
    @Column(name = "grade_level", length = 8)
    private String gradeLevel;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    public Long getId() {
        return id;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLoginUsername() {
        return loginUsername;
    }

    public void setLoginUsername(String loginUsername) {
        this.loginUsername = loginUsername;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getParentPasswordPlain() {
        return parentPasswordPlain;
    }

    public void setParentPasswordPlain(String parentPasswordPlain) {
        this.parentPasswordPlain = parentPasswordPlain;
    }

    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(BigDecimal hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public String getGradeLevel() {
        return gradeLevel;
    }

    public void setGradeLevel(String gradeLevel) {
        this.gradeLevel = gradeLevel;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
