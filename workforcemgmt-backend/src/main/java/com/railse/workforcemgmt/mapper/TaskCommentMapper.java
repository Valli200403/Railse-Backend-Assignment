// src/main/java/com/railse/workforcemgmt/mapper/TaskCommentMapper.java
package com.railse.workforcemgmt.mapper;

import com.railse.workforcemgmt.dto.AddCommentRequest;
import com.railse.workforcemgmt.dto.TaskCommentDto;
import com.railse.workforcemgmt.model.TaskComment;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskCommentMapper {
    TaskCommentDto toDto(TaskComment taskComment);
    List<TaskCommentDto> toDtoList(List<TaskComment> commentList);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "timestamp", ignore = true)
    @Mapping(target = "task", ignore = true)
    TaskComment toEntity(AddCommentRequest addCommentRequest);
}
