// src/main/java/com/railse/workforcemgmt/mapper/TaskActivityMapper.java
package com.railse.workforcemgmt.mapper;

import com.railse.workforcemgmt.dto.TaskActivityDto;
import com.railse.workforcemgmt.model.TaskActivity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskActivityMapper {
    TaskActivityDto toDto(TaskActivity taskActivity);
    List<TaskActivityDto> toDtoList(List<TaskActivity> activityList);
}
