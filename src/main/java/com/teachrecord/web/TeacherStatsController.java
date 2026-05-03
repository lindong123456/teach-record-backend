package com.teachrecord.web;

import com.teachrecord.service.StatsService;
import com.teachrecord.web.dto.StatsDtos;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/teacher/stats")
public class TeacherStatsController {

    private final StatsService statsService;

    public TeacherStatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping
    public StatsDtos.StatsResponse stats(
            @RequestParam @NotNull LocalDate from,
            @RequestParam @NotNull LocalDate to,
            @RequestParam(required = false) Long studentId) {
        return statsService.forTeacher(SecurityUtils.currentTeacherId(), from, to, studentId);
    }
}
