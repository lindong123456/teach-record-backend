package com.teachrecord.web;

import com.teachrecord.service.LessonService;
import com.teachrecord.web.dto.LessonDtos;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/teacher/lessons")
public class TeacherLessonController {

    private final LessonService lessonService;

    public TeacherLessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @GetMapping
    public List<LessonDtos.LessonView> list(@RequestParam(required = false) Long studentId) {
        return lessonService.listForTeacher(SecurityUtils.currentTeacherId(), studentId);
    }

    @PostMapping
    public LessonDtos.LessonView create(@RequestBody @Valid LessonDtos.LessonCreate request) {
        return lessonService.create(SecurityUtils.currentTeacherId(), request);
    }

    @PatchMapping("/{id}")
    public LessonDtos.LessonView update(
            @PathVariable long id, @RequestBody @Valid LessonDtos.LessonUpdate request) {
        return lessonService.update(SecurityUtils.currentTeacherId(), id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        lessonService.delete(SecurityUtils.currentTeacherId(), id);
    }

    @PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public LessonDtos.LessonImageView addImage(
            @PathVariable long id, @RequestParam("file") MultipartFile file) {
        return lessonService.addImage(SecurityUtils.currentTeacherId(), id, file);
    }

    @DeleteMapping("/{lessonId}/images/{imageId}")
    @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
    public void removeImage(
            @PathVariable long lessonId, @PathVariable long imageId) {
        lessonService.removeImage(SecurityUtils.currentTeacherId(), lessonId, imageId);
    }
}
