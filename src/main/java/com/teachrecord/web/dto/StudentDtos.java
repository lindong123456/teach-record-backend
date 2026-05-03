package com.teachrecord.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public class StudentDtos {

    public record StudentCreate(
            @NotBlank @Size(max = 128) String name,
            @NotNull @DecimalMin("0") BigDecimal hourlyRate,
            /** Optional; if blank, server defaults to P1. Singapore levels: P1…P6, S1…S5, JC1, JC2. */
            @Size(max = 8) String gradeLevel) {}

    /**
     * 教师端学生列表/详情 JSON。务必使用 JavaBean getter 序列化：仅用 record 时，部分环境下 {@code
     * parentPassword} 会从响应中整条丢失。
     */
    public static final class StudentView {

        private final long id;
        private final String name;
        private final String loginUsername;
        private final BigDecimal hourlyRate;
        private final String gradeLevel;
        private final String parentPassword;

        public StudentView(
                long id,
                String name,
                String loginUsername,
                BigDecimal hourlyRate,
                String gradeLevel,
                String parentPassword) {
            this.id = id;
            this.name = name;
            this.loginUsername = loginUsername;
            this.hourlyRate = hourlyRate;
            this.gradeLevel = gradeLevel;
            this.parentPassword = parentPassword;
        }

        public long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @JsonProperty("loginUsername")
        public String getLoginUsername() {
            return loginUsername;
        }

        @JsonProperty("hourlyRate")
        public BigDecimal getHourlyRate() {
            return hourlyRate;
        }

        /** 家长登录明文密码；库中为 null 时 JSON 仍为 {@code "parentPassword": null}。 */
        @JsonProperty("parentPassword")
        @JsonInclude(JsonInclude.Include.ALWAYS)
        public String getParentPassword() {
            return parentPassword;
        }

        @JsonProperty("gradeLevel")
        public String getGradeLevel() {
            return gradeLevel;
        }
    }
}
