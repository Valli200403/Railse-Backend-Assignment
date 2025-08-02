// src/main/java/com/railse/workforcemgmt/dto/TaskActivityDto.java
package com.railse.workforcemgmt.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TaskActivityDto {
    private Long id;
    private String message;
    private LocalDateTime timestamp;
}
