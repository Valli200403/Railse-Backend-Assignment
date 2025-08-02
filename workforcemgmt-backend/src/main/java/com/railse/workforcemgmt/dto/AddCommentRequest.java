// src/main/java/com/railse/workforcemgmt/dto/AddCommentRequest.java
package com.railse.workforcemgmt.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddCommentRequest {
    @NotBlank(message = "Author cannot be empty")
    private String author;

    @NotBlank(message = "Comment cannot be empty")
    private String comment;
}
