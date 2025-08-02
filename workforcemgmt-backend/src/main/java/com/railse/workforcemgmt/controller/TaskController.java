// src/main/java/com/railse/workforcemgmt/controller/TaskController.java
package com.railse.workforcemgmt.controller;

import com.railse.workforcemgmt.dto.AddCommentRequest;
import com.railse.workforcemgmt.dto.CreateTaskRequest;
import com.railse.workforcemgmt.dto.TaskCommentDto;
import com.railse.workforcemgmt.dto.TaskDto;
import com.railse.workforcemgmt.entity.Priority;
import com.railse.workforcemgmt.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<TaskDto>> getAllTasks(
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        if (active != null && active) {
            return ResponseEntity.ok(taskService.getActiveTasks());
        }
        if (startDate != null && endDate != null) {
            return ResponseEntity.ok(taskService.getTasksForDateRange(startDate, endDate));
        }
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskByIdWithDetails(id));
    }

    @PostMapping
    public ResponseEntity<TaskDto> createTask(@Valid @RequestBody CreateTaskRequest createTaskRequest) {
        TaskDto createdTask = taskService.createTask(createTaskRequest);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDto> updateTask(@PathVariable Long id, @Valid @RequestBody TaskDto taskDetails) {
        TaskDto updatedTask = taskService.updateTask(id, taskDetails);
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reassign")
    public ResponseEntity<TaskDto> reassignTask(@PathVariable Long id, @RequestParam String newAssignee) {
        TaskDto reassignedTask = taskService.reassignTask(id, newAssignee);
        return ResponseEntity.ok(reassignedTask);
    }

    @PatchMapping("/{id}/priority")
    public ResponseEntity<TaskDto> updateTaskPriority(@PathVariable Long id, @RequestParam Priority newPriority) {
        TaskDto updatedTask = taskService.updateTaskPriority(id, newPriority);
        return ResponseEntity.ok(updatedTask);
    }

    @GetMapping("/priority/{level}")
    public ResponseEntity<List<TaskDto>> getTasksByPriority(@PathVariable Priority level) {
        List<TaskDto> tasks = taskService.getTasksByPriority(level);
        return ResponseEntity.ok(tasks);
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<TaskCommentDto> addCommentToTask(@PathVariable Long id, @Valid @RequestBody AddCommentRequest addCommentRequest) {
        TaskCommentDto commentDto = taskService.addComment(id, addCommentRequest);
        return new ResponseEntity<>(commentDto, HttpStatus.CREATED);
    }
}
