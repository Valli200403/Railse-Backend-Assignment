// src/main/java/com/railse/workforcemgmt/repository/TaskActivityRepository.java
package com.railse.workforcemgmt.repository;

import com.railse.workforcemgmt.model.TaskActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskActivityRepository extends JpaRepository<TaskActivity, Long> {
}
