// src/main/java/com/railse/workforcemgmt/service/TaskService.java
package com.railse.workforcemgmt.service;

import com.railse.workforcemgmt.dto.AddCommentRequest;
import com.railse.workforcemgmt.dto.CreateTaskRequest;
import com.railse.workforcemgmt.dto.TaskCommentDto;
import com.railse.workforcemgmt.dto.TaskDto;
import com.railse.workforcemgmt.entity.Priority;
import com.railse.workforcemgmt.exception.ResourceNotFoundException;
import com.railse.workforcemgmt.mapper.TaskActivityMapper;
import com.railse.workforcemgmt.mapper.TaskCommentMapper;
import com.railse.workforcemgmt.mapper.TaskMapper;
import com.railse.workforcemgmt.model.Task;
import com.railse.workforcemgmt.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final TaskActivityService taskActivityService;
    private final TaskCommentService taskCommentService;
    private final TaskActivityMapper taskActivityMapper;
    private final TaskCommentMapper taskCommentMapper;


    public TaskService(TaskRepository taskRepository, TaskMapper taskMapper,
                       TaskActivityService taskActivityService, TaskCommentService taskCommentService,
                       TaskActivityMapper taskActivityMapper, TaskCommentMapper taskCommentMapper) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
        this.taskActivityService = taskActivityService;
        this.taskCommentService = taskCommentService;
        this.taskActivityMapper = taskActivityMapper;
        this.taskCommentMapper = taskCommentMapper;
    }

    private void logActivity(Task task, String message) {
        taskActivityService.logActivity(task, message);
    }

    public List<TaskDto> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<TaskDto> getActiveTasks() {
        return taskRepository.findByCancelledFalse().stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<TaskDto> getTasksForDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return taskRepository.findActiveTasksForDateRange(startDate, endDate).stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }

    public TaskDto getTaskByIdWithDetails(Long id) {
        Task task = taskRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        TaskDto taskDto = taskMapper.toDto(task);
        if (task.getActivityLog() != null) {
            taskDto.setActivityLog(taskActivityMapper.toDtoList(task.getActivityLog()));
        }
        if (task.getComments() != null) {
            taskDto.setComments(taskCommentMapper.toDtoList(task.getComments()));
        }
        return taskDto;
    }

    public TaskDto getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        return taskMapper.toDto(task);
    }

    @Transactional
    public TaskDto createTask(CreateTaskRequest createTaskRequest) {
        Task task = taskMapper.toEntity(createTaskRequest);
        task.setCancelled(false);
        Task savedTask = taskRepository.save(task);
        logActivity(savedTask, "Task created: " + savedTask.getTitle());
        return taskMapper.toDto(savedTask);
    }

    @Transactional
    public TaskDto updateTask(Long id, TaskDto taskDetails) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        if (taskDetails.getTitle() != null) task.setTitle(taskDetails.getTitle());
        if (taskDetails.getDescription() != null) task.setDescription(taskDetails.getDescription());
        if (taskDetails.getStartDate() != null) task.setStartDate(taskDetails.getStartDate());
        if (taskDetails.getDueDate() != null) task.setDueDate(taskDetails.getDueDate());
        if (taskDetails.getPriority() != null && !taskDetails.getPriority().equals(task.getPriority())) {
            logActivity(task, "Priority changed from " + task.getPriority() + " to " + taskDetails.getPriority());
            task.setPriority(taskDetails.getPriority());
        }
        if (taskDetails.isCancelled() != task.isCancelled()) {
            logActivity(task, "Task status changed to CANCELLED: " + taskDetails.isCancelled());
            task.setCancelled(taskDetails.isCancelled());
        }

        Task updatedTask = taskRepository.save(task);
        logActivity(updatedTask, "Task updated: " + updatedTask.getTitle());
        return taskMapper.toDto(updatedTask);
    }

    @Transactional
    public TaskDto reassignTask(Long taskId, String newAssignee) {
        Task originalTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Original task not found with id: " + taskId));

        if (originalTask.isCancelled()) {
            throw new IllegalStateException("Cannot reassign a cancelled task.");
        }
        if (originalTask.getAssignee().equals(newAssignee)) {
            throw new IllegalArgumentException("Task is already assigned to " + newAssignee);
        }

        originalTask.setCancelled(true);
        taskRepository.save(originalTask);
        logActivity(originalTask, "Task marked as CANCELLED due to reassignment from " + originalTask.getAssignee());

        Task newTask = taskMapper.copyForReassignment(originalTask);
        newTask.setAssignee(newAssignee);
        newTask.setTitle(originalTask.getTitle() + " (Reassigned)");
        newTask.setStartDate(LocalDateTime.now());
        newTask.setDueDate(originalTask.getDueDate());

        Task savedNewTask = taskRepository.save(newTask);
        logActivity(savedNewTask, "New task created for reassignment to " + newAssignee);

        return taskMapper.toDto(savedNewTask);
    }

    @Transactional
    public TaskDto updateTaskPriority(Long id, Priority newPriority) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        if (task.getPriority().equals(newPriority)) {
            throw new IllegalArgumentException("Task already has priority: " + newPriority);
        }

        logActivity(task, "Priority changed from " + task.getPriority() + " to " + newPriority);
        task.setPriority(newPriority);
        Task updatedTask = taskRepository.save(task);
        return taskMapper.toDto(updatedTask);
    }

    public List<TaskDto> getTasksByPriority(Priority priority) {
        return taskRepository.findByPriorityAndCancelledFalse(priority).stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        taskRepository.delete(task);
        logActivity(task, "Task deleted: " + task.getTitle());
    }

    @Transactional
    public TaskCommentDto addComment(Long taskId, AddCommentRequest addCommentRequest) {
        return taskCommentService.addCommentToTask(taskId, addCommentRequest);
    }
}
