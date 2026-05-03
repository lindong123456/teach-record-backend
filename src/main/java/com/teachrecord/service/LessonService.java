package com.teachrecord.service;

import com.teachrecord.domain.Lesson;
import com.teachrecord.domain.LessonImage;
import com.teachrecord.domain.Student;
import com.teachrecord.repo.LessonImageRepository;
import com.teachrecord.repo.LessonRepository;
import com.teachrecord.repo.StudentRepository;
import com.teachrecord.web.dto.LessonDtos;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class LessonService {

    private static final DateTimeFormatter LESSON_TIME_DISPLAY =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:00");

    private final LessonRepository lessonRepository;
    private final LessonImageRepository imageRepository;
    private final StudentRepository studentRepository;
    private final FileStorageService fileStorageService;

    public LessonService(
            LessonRepository lessonRepository,
            LessonImageRepository imageRepository,
            StudentRepository studentRepository,
            FileStorageService fileStorageService) {
        this.lessonRepository = lessonRepository;
        this.imageRepository = imageRepository;
        this.studentRepository = studentRepository;
        this.fileStorageService = fileStorageService;
    }

    /** 整点小时；非整点输入在保存时截断到该小时整点。 */
    public static LocalDateTime alignToHour(LocalDateTime t) {
        if (t == null) {
            return null;
        }
        return t.truncatedTo(ChronoUnit.HOURS);
    }

    @Transactional
    public LessonDtos.LessonView create(long teacherId, LessonDtos.LessonCreate req) {
        Student s = studentRepository
                .findByIdAndTeacherId(req.studentId(), teacherId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "student"));
        Lesson l = new Lesson();
        l.setTeacherId(teacherId);
        l.setStudentId(s.getId());
        l.setLessonTime(alignToHour(Objects.requireNonNull(req.lessonTime())));
        l.setHours(req.hours());
        l.setUnitPrice(s.getHourlyRate());
        l.setSettled(false);
        l.setNotes(req.notes() != null ? req.notes().trim() : null);
        lessonRepository.save(l);
        return toView(l, s.getName(), s.getGradeLevel());
    }

    @Transactional
    public LessonDtos.LessonView update(long teacherId, long id, LessonDtos.LessonUpdate req) {
        Lesson l = lessonRepository
                .findByIdAndTeacherId(id, teacherId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (req.lessonTime() != null) {
            l.setLessonTime(alignToHour(req.lessonTime()));
        }
        if (req.hours() != null) {
            l.setHours(req.hours());
        }
        if (req.notes() != null) {
            l.setNotes(req.notes().isEmpty() ? null : req.notes().trim());
        }
        if (req.settled() != null) {
            l.setSettled(req.settled());
        }
        if (req.hours() != null) {
            Student s = studentRepository
                    .findByIdAndTeacherId(l.getStudentId(), teacherId)
                    .orElseThrow();
            l.setUnitPrice(s.getHourlyRate());
        }
        return studentRepository
                .findByIdAndTeacherId(l.getStudentId(), teacherId)
                .map(st -> toView(l, st.getName(), st.getGradeLevel()))
                .orElseGet(() -> toView(l, "?", null));
    }

    @Transactional
    public void delete(long teacherId, long id) {
        Lesson l = lessonRepository
                .findByIdAndTeacherId(id, teacherId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        imageRepository
                .findByLessonIdOrderByCreatedAtAsc(id)
                .forEach(img -> fileStorageService.deleteIfExists(img.getStoredFilename()));
        imageRepository.deleteByLessonId(id);
        lessonRepository.delete(l);
    }

    @Transactional
    public LessonDtos.LessonImageView addImage(long teacherId, long lessonId, MultipartFile file) {
        lessonRepository
                .findByIdAndTeacherId(lessonId, teacherId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        String stored = fileStorageService.store(file);
        LessonImage img = new LessonImage();
        img.setLessonId(lessonId);
        img.setStoredFilename(stored);
        img.setOriginalFilename(
                file.getOriginalFilename() != null ? file.getOriginalFilename() : "file");
        imageRepository.save(img);
        return new LessonDtos.LessonImageView(
                img.getId(), img.getOriginalFilename(), publicUrl(stored));
    }

    @Transactional
    public void removeImage(long teacherId, long lessonId, long imageId) {
        lessonRepository
                .findByIdAndTeacherId(lessonId, teacherId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        LessonImage img = imageRepository
                .findById(imageId)
                .filter(i -> i.getLessonId() == lessonId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        fileStorageService.deleteIfExists(img.getStoredFilename());
        imageRepository.delete(img);
    }

    public List<LessonDtos.LessonView> listForTeacher(long teacherId, Long studentId) {
        var lessons =
                studentId == null
                        ? lessonRepository.findByTeacherIdOrderByLessonTimeDescCreatedAtDesc(teacherId)
                        : lessonRepository.findByTeacherIdAndStudentIdOrderByLessonTimeDescCreatedAtDesc(
                                teacherId, studentId);
        return lessons.stream().map(this::toViewTeacher).toList();
    }

    public List<LessonDtos.LessonView> listForParent(long studentId) {
        return lessonRepository.findByStudentIdOrderByLessonTimeDescCreatedAtDesc(studentId).stream()
                .map(this::toViewParent)
                .toList();
    }

    private LessonDtos.LessonView toViewTeacher(Lesson l) {
        return studentRepository
                .findByIdAndTeacherId(l.getStudentId(), l.getTeacherId())
                .map(st -> toView(l, st.getName(), st.getGradeLevel()))
                .orElseGet(() -> toView(l, "?", null));
    }

    private LessonDtos.LessonView toViewParent(Lesson l) {
        return studentRepository
                .findById(l.getStudentId())
                .map(s -> toView(l, s.getName(), s.getGradeLevel()))
                .orElseGet(() -> toView(l, "?", null));
    }

    private LessonDtos.LessonView toView(
            Lesson l, String studentName, String studentGradeLevel) {
        var imgs = imageRepository.findByLessonIdOrderByCreatedAtAsc(l.getId());
        return new LessonDtos.LessonView(
                l.getId(),
                l.getStudentId(),
                studentName,
                studentGradeLevel,
                LESSON_TIME_DISPLAY.format(l.getLessonTime()),
                l.getHours(),
                l.getUnitPrice(),
                l.isSettled(),
                l.getNotes(),
                l.getCreatedAt().toString(),
                imgs.stream()
                        .map(
                                i ->
                                        new LessonDtos.LessonImageView(
                                                i.getId(), i.getOriginalFilename(), publicUrl(i.getStoredFilename())))
                        .toList());
    }

    public static String publicUrl(String storedFilename) {
        return "/uploads/" + storedFilename;
    }
}
