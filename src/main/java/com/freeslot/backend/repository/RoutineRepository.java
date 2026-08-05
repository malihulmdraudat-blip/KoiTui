package com.freeslot.backend.repository;

import com.freeslot.backend.entity.Routine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoutineRepository extends JpaRepository<Routine, Long> {
    List<Routine> findByUserId(Long userId);
    List<Routine> findByUserIdIn(List<Long> userIds);
    void deleteByIdAndUserId(Long id, Long userId);
}
