// src/main/java/com/railse/workforcemgmt/mapper/TaskMapper.java
package com.railse.workforcemgmt.mapper;

import com.railse.workforcemgmt.dto.CreateTaskRequest;
import com.railse.workforcemgmt.dto.TaskDto;
import com.railse.workforcemgmt.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {TaskActivityMapper.class, TaskCommentMapper.class})
public interface TaskMapper {
    Task toEntity(TaskDto taskDto);
    TaskDto toDto(Task task);

    Task toEntity(CreateTaskRequest createTaskRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "activityLog", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "cancelled", constant = "false")
    Task copyForReassignment(Task originalTask);
}
