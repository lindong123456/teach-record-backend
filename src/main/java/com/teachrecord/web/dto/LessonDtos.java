package com.teachrecord.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class LessonDtos {

    public record LessonCreate(
            @NotNull Long studentId,
            @NotNull LocalDateTime lessonTime,
            @NotNull @DecimalMin("0.01") BigDecimal hours,
            String notes) {}

    public record LessonUpdate(
            LocalDateTime lessonTime,
            @DecimalMin("0.01") BigDecimal hours,
            String notes,
            Boolean settled) {}

    public record LessonView(
            long id,
            long studentId,
            String studentName,
            String studentGradeLevel,
            String lessonTime,
            BigDecimal hours,
            BigDecimal unitPrice,
            boolean settled,
            String notes,
            String createdAt,
            List<LessonImageView> images) {}

    public record LessonImageView(
            long id, String originalFilename, String publicUrl) {}
}
