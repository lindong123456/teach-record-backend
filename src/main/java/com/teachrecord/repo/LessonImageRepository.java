package com.teachrecord.repo;

import com.teachrecord.domain.LessonImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LessonImageRepository extends JpaRepository<LessonImage, Long> {

    List<LessonImage> findByLessonIdOrderByCreatedAtAsc(Long lessonId);

    @Modifying(clearAutomatically = true)
    @Query("delete from LessonImage i where i.lessonId = :lessonId")
    void deleteByLessonId(@Param("lessonId") Long lessonId);
}
