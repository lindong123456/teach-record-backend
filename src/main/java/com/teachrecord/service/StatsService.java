package com.teachrecord.service;

import com.teachrecord.domain.Lesson;
import com.teachrecord.domain.Student;
import com.teachrecord.repo.LessonRepository;
import com.teachrecord.repo.StudentRepository;
import com.teachrecord.web.dto.StatsDtos;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class StatsService {

    private static final BigDecimal Z = BigDecimal.ZERO;

    private final LessonRepository lessonRepository;
    private final StudentRepository studentRepository;

    public StatsService(LessonRepository lessonRepository, StudentRepository studentRepository) {
        this.lessonRepository = lessonRepository;
        this.studentRepository = studentRepository;
    }

    public StatsDtos.StatsResponse forTeacher(
            long teacherId, LocalDate from, LocalDate to, Long studentIdFilter) {
        if (to.isBefore(from)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "to before from");
        }
        if (studentIdFilter != null) {
            studentRepository
                    .findByIdAndTeacherId(studentIdFilter, teacherId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "student"));
        }
        return aggregate(
                lessonRepository.findForStats(
                        teacherId, from.atStartOfDay(), to.plusDays(1).atStartOfDay(), studentIdFilter),
                teacherId);
    }

    public StatsDtos.StatsResponse forParent(
            long teacherId, long studentId, LocalDate from, LocalDate to) {
        if (to.isBefore(from)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "to before from");
        }
        studentRepository
                .findByIdAndTeacherId(studentId, teacherId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN));
        return aggregate(
                lessonRepository.findForStats(
                        teacherId, from.atStartOfDay(), to.plusDays(1).atStartOfDay(), studentId),
                teacherId);
    }

    private StatsDtos.StatsResponse aggregate(List<Lesson> lessons, long teacherId) {
        Map<Long, String> names = new HashMap<>();
        for (Student st : studentRepository.findByTeacherIdOrderByCreatedAtDesc(teacherId)) {
            names.put(st.getId(), st.getName());
        }

        record DayKey(long studentId, LocalDate d) {}
        record MonthKey(long studentId, int y, int m) {}

        Map<DayKey, BigDecimal[]> byDay = new HashMap<>();
        Map<MonthKey, BigDecimal[]> byMonth = new HashMap<>();
        BigDecimal unsettledHours = Z;
        BigDecimal unsettledAmount = Z;
        BigDecimal settledHours = Z;
        BigDecimal settledAmount = Z;

        for (Lesson l : lessons) {
            BigDecimal amt = l.getHours().multiply(l.getUnitPrice());
            if (l.isSettled()) {
                settledHours = settledHours.add(l.getHours());
                settledAmount = settledAmount.add(amt);
            } else {
                unsettledHours = unsettledHours.add(l.getHours());
                unsettledAmount = unsettledAmount.add(amt);
            }
            var lt = l.getLessonTime();
            var day = lt.toLocalDate();
            DayKey dk = new DayKey(l.getStudentId(), day);
            add(byDay, dk, l.getHours(), amt);
            int y = lt.getYear();
            int m = lt.getMonthValue();
            add(byMonth, new MonthKey(l.getStudentId(), y, m), l.getHours(), amt);
        }

        List<StatsDtos.DayPoint> dayList = new ArrayList<>();
        for (var e : byDay.entrySet()) {
            DayKey k = e.getKey();
            BigDecimal[] v = e.getValue();
            dayList.add(
                    new StatsDtos.DayPoint(
                            k.studentId, names.getOrDefault(k.studentId, "?"), k.d.toString(), v[0], v[1]));
        }
        dayList.sort(Comparator.comparing(StatsDtos.DayPoint::date).thenComparing(StatsDtos.DayPoint::studentName));

        List<StatsDtos.MonthPoint> monthList = new ArrayList<>();
        for (var e : byMonth.entrySet()) {
            MonthKey k = e.getKey();
            BigDecimal[] v = e.getValue();
            monthList.add(
                    new StatsDtos.MonthPoint(
                            k.studentId, names.getOrDefault(k.studentId, "?"), k.y, k.m, v[0], v[1]));
        }
        monthList.sort(
                Comparator.comparing(StatsDtos.MonthPoint::year)
                        .thenComparing(StatsDtos.MonthPoint::month)
                        .thenComparing(StatsDtos.MonthPoint::studentName));
        var settlement =
                new StatsDtos.SettlementSummary(
                        unsettledHours, unsettledAmount, settledHours, settledAmount);
        return new StatsDtos.StatsResponse(dayList, monthList, settlement);
    }

    private static <K> void add(Map<K, BigDecimal[]> map, K key, BigDecimal hours, BigDecimal amount) {
        BigDecimal[] a = map.computeIfAbsent(key, k -> new BigDecimal[] {Z, Z});
        a[0] = a[0].add(hours);
        a[1] = a[1].add(amount);
    }
}
