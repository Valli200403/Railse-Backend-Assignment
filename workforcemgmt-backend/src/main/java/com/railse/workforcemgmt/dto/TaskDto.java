// src/main/java/com/railse/workforcemgmt/dto/TaskDto.java
package com.railse.workforcemgmt.dto;

import com.railse.workforcemgmt.entity.Priority;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TaskDto {
    private Long id;
    private String title;
    private String description;
    private String assignee;
    private LocalDateTime startDate;
    private LocalDateTime dueDate;
    private Priority priority;
    private boolean cancelled;
    private LocalDateTime creationDate;
    private LocalDateTime lastModifiedDate;
    private List<TaskActivityDto> activityLog;
    private List<TaskCommentDto> comments;

    // Feature 1: Smart Due Date Calculation
    // This is a calculated field, not stored in the database directly.
    public String getSmartDueDate() {
        if (dueDate == null) {
            return "No due date";
        }
        LocalDateTime now = LocalDateTime.now();
        if (dueDate.isBefore(now)) {
            return "Overdue";
        }
        if (dueDate.isBefore(now.plusDays(1))) {
            return "Due today";
        }
        if (dueDate.isBefore(now.plusDays(7))) {
            return "Due this week";
        }
        return "Due later";
    }
}
