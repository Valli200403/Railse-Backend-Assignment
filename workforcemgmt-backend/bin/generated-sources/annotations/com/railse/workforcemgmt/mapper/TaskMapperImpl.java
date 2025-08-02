package com.railse.workforcemgmt.mapper;

import com.railse.workforcemgmt.dto.CreateTaskRequest;
import com.railse.workforcemgmt.dto.TaskActivityDto;
import com.railse.workforcemgmt.dto.TaskCommentDto;
import com.railse.workforcemgmt.dto.TaskDto;
import com.railse.workforcemgmt.model.Task;
import com.railse.workforcemgmt.model.TaskActivity;
import com.railse.workforcemgmt.model.TaskComment;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-02T15:14:07+0000",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.42.50.v20250729-0351, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class TaskMapperImpl implements TaskMapper {

    @Autowired
    private TaskActivityMapper taskActivityMapper;
    @Autowired
    private TaskCommentMapper taskCommentMapper;

    @Override
    public Task toEntity(TaskDto taskDto) {
        if ( taskDto == null ) {
            return null;
        }

        Task task = new Task();

        task.setActivityLog( taskActivityDtoListToTaskActivityList( taskDto.getActivityLog() ) );
        task.setAssignee( taskDto.getAssignee() );
        task.setCancelled( taskDto.isCancelled() );
        task.setComments( taskCommentDtoListToTaskCommentList( taskDto.getComments() ) );
        task.setCreationDate( taskDto.getCreationDate() );
        task.setDescription( taskDto.getDescription() );
        task.setDueDate( taskDto.getDueDate() );
        task.setId( taskDto.getId() );
        task.setLastModifiedDate( taskDto.getLastModifiedDate() );
        task.setPriority( taskDto.getPriority() );
        task.setStartDate( taskDto.getStartDate() );
        task.setTitle( taskDto.getTitle() );

        return task;
    }

    @Override
    public TaskDto toDto(Task task) {
        if ( task == null ) {
            return null;
        }

        TaskDto taskDto = new TaskDto();

        taskDto.setActivityLog( taskActivityMapper.toDtoList( task.getActivityLog() ) );
        taskDto.setAssignee( task.getAssignee() );
        taskDto.setCancelled( task.isCancelled() );
        taskDto.setComments( taskCommentMapper.toDtoList( task.getComments() ) );
        taskDto.setCreationDate( task.getCreationDate() );
        taskDto.setDescription( task.getDescription() );
        taskDto.setDueDate( task.getDueDate() );
        taskDto.setId( task.getId() );
        taskDto.setLastModifiedDate( task.getLastModifiedDate() );
        taskDto.setPriority( task.getPriority() );
        taskDto.setStartDate( task.getStartDate() );
        taskDto.setTitle( task.getTitle() );

        return taskDto;
    }

    @Override
    public Task toEntity(CreateTaskRequest createTaskRequest) {
        if ( createTaskRequest == null ) {
            return null;
        }

        Task task = new Task();

        task.setAssignee( createTaskRequest.getAssignee() );
        task.setDescription( createTaskRequest.getDescription() );
        task.setDueDate( createTaskRequest.getDueDate() );
        task.setPriority( createTaskRequest.getPriority() );
        task.setStartDate( createTaskRequest.getStartDate() );
        task.setTitle( createTaskRequest.getTitle() );

        return task;
    }

    @Override
    public Task copyForReassignment(Task originalTask) {
        if ( originalTask == null ) {
            return null;
        }

        Task task = new Task();

        task.setAssignee( originalTask.getAssignee() );
        task.setDescription( originalTask.getDescription() );
        task.setDueDate( originalTask.getDueDate() );
        task.setPriority( originalTask.getPriority() );
        task.setStartDate( originalTask.getStartDate() );
        task.setTitle( originalTask.getTitle() );

        task.setCancelled( false );

        return task;
    }

    protected TaskActivity taskActivityDtoToTaskActivity(TaskActivityDto taskActivityDto) {
        if ( taskActivityDto == null ) {
            return null;
        }

        TaskActivity taskActivity = new TaskActivity();

        taskActivity.setId( taskActivityDto.getId() );
        taskActivity.setMessage( taskActivityDto.getMessage() );
        taskActivity.setTimestamp( taskActivityDto.getTimestamp() );

        return taskActivity;
    }

    protected List<TaskActivity> taskActivityDtoListToTaskActivityList(List<TaskActivityDto> list) {
        if ( list == null ) {
            return null;
        }

        List<TaskActivity> list1 = new ArrayList<TaskActivity>( list.size() );
        for ( TaskActivityDto taskActivityDto : list ) {
            list1.add( taskActivityDtoToTaskActivity( taskActivityDto ) );
        }

        return list1;
    }

    protected TaskComment taskCommentDtoToTaskComment(TaskCommentDto taskCommentDto) {
        if ( taskCommentDto == null ) {
            return null;
        }

        TaskComment taskComment = new TaskComment();

        taskComment.setAuthor( taskCommentDto.getAuthor() );
        taskComment.setComment( taskCommentDto.getComment() );
        taskComment.setId( taskCommentDto.getId() );
        taskComment.setTimestamp( taskCommentDto.getTimestamp() );

        return taskComment;
    }

    protected List<TaskComment> taskCommentDtoListToTaskCommentList(List<TaskCommentDto> list) {
        if ( list == null ) {
            return null;
        }

        List<TaskComment> list1 = new ArrayList<TaskComment>( list.size() );
        for ( TaskCommentDto taskCommentDto : list ) {
            list1.add( taskCommentDtoToTaskComment( taskCommentDto ) );
        }

        return list1;
    }
}
