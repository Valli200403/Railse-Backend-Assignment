package com.railse.workforcemgmt.mapper;

import com.railse.workforcemgmt.dto.TaskActivityDto;
import com.railse.workforcemgmt.model.TaskActivity;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-02T15:14:06+0000",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.42.50.v20250729-0351, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class TaskActivityMapperImpl implements TaskActivityMapper {

    @Override
    public TaskActivityDto toDto(TaskActivity taskActivity) {
        if ( taskActivity == null ) {
            return null;
        }

        TaskActivityDto taskActivityDto = new TaskActivityDto();

        taskActivityDto.setId( taskActivity.getId() );
        taskActivityDto.setMessage( taskActivity.getMessage() );
        taskActivityDto.setTimestamp( taskActivity.getTimestamp() );

        return taskActivityDto;
    }

    @Override
    public List<TaskActivityDto> toDtoList(List<TaskActivity> activityList) {
        if ( activityList == null ) {
            return null;
        }

        List<TaskActivityDto> list = new ArrayList<TaskActivityDto>( activityList.size() );
        for ( TaskActivity taskActivity : activityList ) {
            list.add( toDto( taskActivity ) );
        }

        return list;
    }
}
