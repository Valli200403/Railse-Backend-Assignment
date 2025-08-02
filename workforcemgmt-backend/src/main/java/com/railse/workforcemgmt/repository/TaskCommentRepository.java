// src/main/java/com/railse/workforcemgmt/repository/TaskCommentRepository.java
package com.railse.workforcemgmt.repository;

import com.railse.workforcemgmt.model.TaskComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskCommentRepository extends JpaRepository<TaskComment, Long> {
}
