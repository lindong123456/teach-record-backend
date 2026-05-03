package com.teachrecord.repo;

import com.teachrecord.domain.Teacher;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    Optional<Teacher> findByUsername(String username);

    boolean existsByUsername(String username);
}
