// src/main/java/com/railse/workforcemgmt/service/TaskCommentService.java
package com.railse.workforcemgmt.service;

import com.railse.workforcemgmt.dto.AddCommentRequest;
import com.railse.workforcemgmt.dto.TaskCommentDto;
import com.railse.workforcemgmt.exception.ResourceNotFoundException;
import com.railse.workforcemgmt.mapper.TaskCommentMapper;
import com.railse.workforcemgmt.model.Task;
import com.railse.workforcemgmt.model.TaskComment;
import com.railse.workforcemgmt.repository.TaskCommentRepository;
import com.railse.workforcemgmt.repository.TaskRepository;
import org.springframework.stereotype.Service;

@Service
public class TaskCommentService {

    private final TaskCommentRepository taskCommentRepository;
    private final TaskRepository taskRepository;
    private final TaskCommentMapper taskCommentMapper;

    public TaskCommentService(TaskCommentRepository taskCommentRepository, TaskRepository taskRepository, TaskCommentMapper taskCommentMapper) {
        this.taskCommentRepository = taskCommentRepository;
        this.taskRepository = taskRepository;
        this.taskCommentMapper = taskCommentMapper;
    }

    public TaskCommentDto addCommentToTask(Long taskId, AddCommentRequest addCommentRequest) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        TaskComment comment = taskCommentMapper.toEntity(addCommentRequest);
        task.addComment(comment);
        TaskComment savedComment = taskCommentRepository.save(comment);
        return taskCommentMapper.toDto(savedComment);
    }
}
