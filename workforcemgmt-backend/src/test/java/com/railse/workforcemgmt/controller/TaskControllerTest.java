// src/test/java/com/railse/workforcemgmt/controller/TaskControllerTest.java
// src/test/java/com/railse/workforcemgmt/controller/TaskControllerTest.java
package com.railse.workforcemgmt.controller;

import com.railse.workforcemgmt.dto.*;
import com.railse.workforcemgmt.entity.Priority;
import com.railse.workforcemgmt.exception.ResourceNotFoundException;
import com.railse.workforcemgmt.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void getAllTasks_shouldReturnAllTasks() throws Exception {
        TaskDto task1 = new TaskDto();
        task1.setId(1L);
        task1.setTitle("Task 1");
        task1.setDueDate(LocalDateTime.now().plusDays(1));
        task1.setPriority(Priority.MEDIUM);

        TaskDto task2 = new TaskDto();
        task2.setId(2L);
        task2.setTitle("Task 2");
        task2.setDueDate(LocalDateTime.now().plusDays(2));
        task2.setPriority(Priority.LOW);
        task2.setCancelled(true);

        List<TaskDto> allTasks = Arrays.asList(task1, task2);
        when(taskService.getAllTasks()).thenReturn(allTasks);

        mockMvc.perform(get("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Task 1"))
                .andExpect(jsonPath("$[0].smartDueDate").exists())
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].title").value("Task 2"))
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getAllTasks_shouldReturnActiveTasksWhenActiveParamIsTrue() throws Exception {
        TaskDto activeTask = new TaskDto();
        activeTask.setId(1L);
        activeTask.setTitle("Active Task");
        activeTask.setCancelled(false);
        activeTask.setDueDate(LocalDateTime.now().plusDays(1));
        activeTask.setPriority(Priority.HIGH);

        List<TaskDto> activeTasks = Collections.singletonList(activeTask);
        when(taskService.getActiveTasks()).thenReturn(activeTasks);

        mockMvc.perform(get("/api/tasks?active=true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Active Task"))
                .andExpect(jsonPath("$[0].cancelled").value(false))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getTasksInDateRange_shouldReturnTasksInRange() throws Exception {
        TaskDto taskInRange = new TaskDto();
        taskInRange.setId(1L);
        taskInRange.setTitle("Task in Range");
        taskInRange.setStartDate(LocalDateTime.of(2025, 1, 15, 0, 0));
        taskInRange.setDueDate(LocalDateTime.of(2025, 1, 20, 0, 0));
        taskInRange.setCancelled(false);

        List<TaskDto> tasks = Collections.singletonList(taskInRange);

        when(taskService.getTasksForDateRange(any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(tasks);

        mockMvc.perform(get("/api/tasks/test")
                        .param("start", "2025-01-01T00:00:00")
                        .param("end", "2025-01-31T23:59:59")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Task in Range"))
                .andExpect(jsonPath("$.length()").value(1));
    }

    // Other tests remain unchanged...

    // (keep all your existing create, update, delete, reassign, etc. tests here — unchanged)



    @Test
    void createTask_shouldReturnCreatedTask() throws Exception {
        CreateTaskRequest newTaskRequest = new CreateTaskRequest();
        newTaskRequest.setTitle("New Task");
        newTaskRequest.setDescription("New Description");
        newTaskRequest.setAssignee("Jane Doe");
        newTaskRequest.setStartDate(LocalDateTime.now());
        newTaskRequest.setDueDate(LocalDateTime.now().plusDays(7));
        newTaskRequest.setPriority(Priority.LOW);

        TaskDto createdTaskDto = new TaskDto();
        createdTaskDto.setId(3L);
        createdTaskDto.setTitle("New Task");
        createdTaskDto.setDescription("New Description");
        createdTaskDto.setAssignee("Jane Doe");
        createdTaskDto.setStartDate(LocalDateTime.now());
        createdTaskDto.setDueDate(LocalDateTime.now().plusDays(7));
        createdTaskDto.setPriority(Priority.LOW);
        createdTaskDto.setCancelled(false);

        when(taskService.createTask(any(CreateTaskRequest.class))).thenReturn(createdTaskDto);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTaskRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.title").value("New Task"))
                .andExpect(jsonPath("$.assignee").value("Jane Doe"))
                .andExpect(jsonPath("$.priority").value("LOW"));
    }

    @Test
    void updateTask_shouldReturnUpdatedTask() throws Exception {
        TaskDto updatedDetails = new TaskDto();
        updatedDetails.setTitle("Updated Title");
        updatedDetails.setAssignee("Old Assignee");
        updatedDetails.setPriority(Priority.HIGH);
        updatedDetails.setCancelled(true);
        updatedDetails.setStartDate(LocalDateTime.now().plusDays(1));
        updatedDetails.setDueDate(LocalDateTime.now().plusDays(10));


        TaskDto returnedUpdatedTask = new TaskDto();
        returnedUpdatedTask.setId(1L);
        returnedUpdatedTask.setTitle("Updated Title");
        returnedUpdatedTask.setAssignee("Old Assignee");
        returnedUpdatedTask.setPriority(Priority.HIGH);
        returnedUpdatedTask.setCancelled(true);
        returnedUpdatedTask.setStartDate(LocalDateTime.now().plusDays(1));
        returnedUpdatedTask.setDueDate(LocalDateTime.now().plusDays(10));


        when(taskService.updateTask(eq(1L), any(TaskDto.class))).thenReturn(returnedUpdatedTask);

        mockMvc.perform(put("/api/tasks/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.assignee").value("Old Assignee"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.cancelled").value(true));
    }

    @Test
    void updateTask_shouldReturnNotFoundWhenTaskDoesNotExist() throws Exception {
        TaskDto updatedDetails = new TaskDto();
        updatedDetails.setTitle("Non Existent");

        when(taskService.updateTask(eq(99L), any(TaskDto.class)))
                .thenThrow(new ResourceNotFoundException("Task not found"));

        mockMvc.perform(put("/api/tasks/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteTask_shouldReturnNoContent() throws Exception {
        doNothing().when(taskService).deleteTask(1L);

        mockMvc.perform(delete("/api/tasks/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteTask_shouldReturnNotFoundWhenTaskDoesNotExist() throws Exception {
        doThrow(new ResourceNotFoundException("Task not found")).when(taskService).deleteTask(99L);

        mockMvc.perform(delete("/api/tasks/{id}", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    void reassignTask_shouldReturnReassignedTask() throws Exception {
        TaskDto reassignedTask = new TaskDto();
        reassignedTask.setId(2L);
        reassignedTask.setTitle("Original Task (Reassigned)");
        reassignedTask.setAssignee("New Assignee");
        reassignedTask.setCancelled(false);

        when(taskService.reassignTask(eq(1L), eq("New Assignee"))).thenReturn(reassignedTask);

        mockMvc.perform(patch("/api/tasks/{id}/reassign", 1L)
                        .param("newAssignee", "New Assignee")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.assignee").value("New Assignee"))
                .andExpect(jsonPath("$.cancelled").value(false));
    }

    @Test
    void updateTaskPriority_shouldReturnUpdatedTask() throws Exception {
        TaskDto updatedTask = new TaskDto();
        updatedTask.setId(1L);
        updatedTask.setTitle("Task 1");
        updatedTask.setPriority(Priority.URGENT);

        when(taskService.updateTaskPriority(eq(1L), eq(Priority.URGENT))).thenReturn(updatedTask);

        mockMvc.perform(patch("/api/tasks/{id}/priority", 1L)
                        .param("newPriority", "URGENT")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.priority").value("URGENT"));
    }

    @Test
    void getTasksByPriority_shouldReturnTasks() throws Exception {
        TaskDto highPriorityTask = new TaskDto();
        highPriorityTask.setId(1L);
        highPriorityTask.setTitle("High Priority");
        highPriorityTask.setPriority(Priority.HIGH);

        List<TaskDto> tasks = Collections.singletonList(highPriorityTask);
        when(taskService.getTasksByPriority(Priority.HIGH)).thenReturn(tasks);

        mockMvc.perform(get("/api/tasks/priority/{level}", "HIGH")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("High Priority"))
                .andExpect(jsonPath("$[0].priority").value("HIGH"));
    }

    @Test
    void addCommentToTask_shouldReturnCreatedComment() throws Exception {
        AddCommentRequest request = new AddCommentRequest();
        request.setAuthor("Commenter");
        request.setComment("Great task!");

        TaskCommentDto createdComment = new TaskCommentDto();
        createdComment.setId(1L);
        createdComment.setAuthor("Commenter");
        createdComment.setComment("Great task!");
        createdComment.setTimestamp(LocalDateTime.now());

        when(taskService.addComment(eq(1L), any(AddCommentRequest.class))).thenReturn(createdComment);

        mockMvc.perform(post("/api/tasks/{id}/comments", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.author").value("Commenter"))
                .andExpect(jsonPath("$.comment").value("Great task!"));
    }
}
