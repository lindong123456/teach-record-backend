package com.teachrecord.web;

import com.teachrecord.service.LessonService;
import com.teachrecord.service.StatsService;
import com.teachrecord.web.dto.LessonDtos;
import com.teachrecord.web.dto.StatsDtos;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/parent")
public class ParentController {

    private final LessonService lessonService;
    private final StatsService statsService;

    public ParentController(LessonService lessonService, StatsService statsService) {
        this.lessonService = lessonService;
        this.statsService = statsService;
    }

    @GetMapping("/lessons")
    public List<LessonDtos.LessonView> lessons() {
        return lessonService.listForParent(SecurityUtils.currentStudentId());
    }

    @GetMapping("/stats")
    public StatsDtos.StatsResponse stats(
            @RequestParam @NotNull LocalDate from, @RequestParam @NotNull LocalDate to) {
        return statsService.forParent(
                SecurityUtils.parentTeacherId(), SecurityUtils.currentStudentId(), from, to);
    }
}
