package com.teachrecord.web;

import com.teachrecord.service.StudentService;
import com.teachrecord.web.dto.StudentDtos;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/teacher/students")
public class TeacherStudentController {

    private final StudentService studentService;

    public TeacherStudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public List<StudentDtos.StudentView> list() {
        return studentService.list(SecurityUtils.currentTeacherId());
    }

    @PostMapping
    public StudentDtos.StudentView create(@RequestBody @Valid StudentDtos.StudentCreate request) {
        return studentService.create(SecurityUtils.currentTeacherId(), request);
    }

    @RequestMapping(value = "/{id}/hourly-rate", method = {RequestMethod.PATCH, RequestMethod.PUT})
    public StudentDtos.StudentView updateRate(@PathVariable long id, @RequestParam BigDecimal rate) {
        return studentService.updateRate(SecurityUtils.currentTeacherId(), id, rate);
    }

    @RequestMapping(value = "/{id}/grade-level", method = {RequestMethod.PATCH, RequestMethod.PUT})
    public StudentDtos.StudentView updateGradeLevel(
            @PathVariable long id, @RequestParam("gradeLevel") String gradeLevel) {
        return studentService.updateGradeLevel(SecurityUtils.currentTeacherId(), id, gradeLevel);
    }

    @PostMapping("/{id}/reset-password")
    public StudentDtos.StudentView resetPassword(@PathVariable long id) {
        return studentService.resetPassword(SecurityUtils.currentTeacherId(), id);
    }
}
