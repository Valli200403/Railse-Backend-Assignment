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
    date = "2025-08-02T14:00:27+0000",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.14.2.jar, environment: Java 21.0.7 (Microsoft)"
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

        task.setId( taskDto.getId() );
        task.setTitle( taskDto.getTitle() );
        task.setDescription( taskDto.getDescription() );
        task.setAssignee( taskDto.getAssignee() );
        task.setStartDate( taskDto.getStartDate() );
        task.setDueDate( taskDto.getDueDate() );
        task.setPriority( taskDto.getPriority() );
        task.setCancelled( taskDto.isCancelled() );
        task.setCreationDate( taskDto.getCreationDate() );
        task.setLastModifiedDate( taskDto.getLastModifiedDate() );
        task.setActivityLog( taskActivityDtoListToTaskActivityList( taskDto.getActivityLog() ) );
        task.setComments( taskCommentDtoListToTaskCommentList( taskDto.getComments() ) );

        return task;
    }

    @Override
    public TaskDto toDto(Task task) {
        if ( task == null ) {
            return null;
        }

        TaskDto taskDto = new TaskDto();

        taskDto.setId( task.getId() );
        taskDto.setTitle( task.getTitle() );
        taskDto.setDescription( task.getDescription() );
        taskDto.setAssignee( task.getAssignee() );
        taskDto.setStartDate( task.getStartDate() );
        taskDto.setDueDate( task.getDueDate() );
        taskDto.setPriority( task.getPriority() );
        taskDto.setCancelled( task.isCancelled() );
        taskDto.setCreationDate( task.getCreationDate() );
        taskDto.setLastModifiedDate( task.getLastModifiedDate() );
        taskDto.setActivityLog( taskActivityMapper.toDtoList( task.getActivityLog() ) );
        taskDto.setComments( taskCommentMapper.toDtoList( task.getComments() ) );

        return taskDto;
    }

    @Override
    public Task toEntity(CreateTaskRequest createTaskRequest) {
        if ( createTaskRequest == null ) {
            return null;
        }

        Task task = new Task();

        task.setTitle( createTaskRequest.getTitle() );
        task.setDescription( createTaskRequest.getDescription() );
        task.setAssignee( createTaskRequest.getAssignee() );
        task.setStartDate( createTaskRequest.getStartDate() );
        task.setDueDate( createTaskRequest.getDueDate() );
        task.setPriority( createTaskRequest.getPriority() );

        return task;
    }

    @Override
    public Task copyForReassignment(Task originalTask) {
        if ( originalTask == null ) {
            return null;
        }

        Task task = new Task();

        task.setTitle( originalTask.getTitle() );
        task.setDescription( originalTask.getDescription() );
        task.setAssignee( originalTask.getAssignee() );
        task.setStartDate( originalTask.getStartDate() );
        task.setDueDate( originalTask.getDueDate() );
        task.setPriority( originalTask.getPriority() );

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

        taskComment.setId( taskCommentDto.getId() );
        taskComment.setAuthor( taskCommentDto.getAuthor() );
        taskComment.setComment( taskCommentDto.getComment() );
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
