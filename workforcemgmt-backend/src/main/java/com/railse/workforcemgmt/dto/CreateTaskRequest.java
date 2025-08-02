// src/main/java/com/railse/workforcemgmt/dto/CreateTaskRequest.java
package com.railse.workforcemgmt.dto;

import com.railse.workforcemgmt.entity.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CreateTaskRequest {
    @NotBlank(message = "Title cannot be empty")
    private String title;

    private String description;

    @NotBlank(message = "Assignee cannot be empty")
    private String assignee;

    @NotNull(message = "Due Date cannot be null")
    private LocalDateTime dueDate;

    @NotNull(message = "Priority cannot be null")
    private Priority priority;

    private LocalDateTime startDate; // Ensure this field is present
}
