// src/main/java/com/railse/workforcemgmt/dto/TaskCommentDto.java
package com.railse.workforcemgmt.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TaskCommentDto {
    private Long id;
    private String author;
    private String comment;
    private LocalDateTime timestamp;
}
