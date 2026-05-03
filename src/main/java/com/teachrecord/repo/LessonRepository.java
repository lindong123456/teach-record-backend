package com.teachrecord.repo;

import com.teachrecord.domain.Lesson;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LessonRepository extends JpaRepository<Lesson, Long> {

    Optional<Lesson> findByIdAndTeacherId(Long id, Long teacherId);

    List<Lesson> findByTeacherIdAndStudentIdOrderByLessonTimeDescCreatedAtDesc(
            Long teacherId, Long studentId);

    List<Lesson> findByTeacherIdOrderByLessonTimeDescCreatedAtDesc(Long teacherId);

    @Query(
            """
            select l from Lesson l
            where l.teacherId = :teacherId
              and l.lessonTime >= :fromBound
              and l.lessonTime < :toExclusive
              and (:studentId is null or l.studentId = :studentId)
            order by l.lessonTime asc, l.id asc
            """)
    List<Lesson> findForStats(
            @Param("teacherId") Long teacherId,
            @Param("fromBound") LocalDateTime fromBound,
            @Param("toExclusive") LocalDateTime toExclusive,
            @Param("studentId") Long studentId);

    List<Lesson> findByStudentIdOrderByLessonTimeDescCreatedAtDesc(Long studentId);
}
