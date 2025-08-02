// src/main/java/com/railse/workforcemgmt/repository/TaskRepository.java
package com.railse.workforcemgmt.repository;

import com.railse.workforcemgmt.entity.Priority;
import com.railse.workforcemgmt.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByCancelledFalse();

    List<Task> findByPriorityAndCancelledFalse(Priority priority);

    @Query("SELECT t FROM Task t WHERE t.cancelled = FALSE " +
           "AND t.startDate <= :endDate " +
           "AND t.dueDate >= :startDate")
    List<Task> findActiveTasksForDateRange(@Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);

    @Query("SELECT t FROM Task t LEFT JOIN FETCH t.comments c LEFT JOIN FETCH t.activityLog a WHERE t.id = :id")
    Optional<Task> findByIdWithDetails(@Param("id") Long id);
}
