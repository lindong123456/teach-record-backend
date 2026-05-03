package com.teachrecord.repo;

import com.teachrecord.domain.Student;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findByTeacherIdOrderByCreatedAtDesc(Long teacherId);

    Optional<Student> findByIdAndTeacherId(Long id, Long teacherId);

    Optional<Student> findByLoginUsername(String loginUsername);

    boolean existsByLoginUsername(String loginUsername);
}
