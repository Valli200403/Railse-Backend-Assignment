// src/test/java/com/railse/workforcemgmt/service/TaskServiceTest.java
package com.railse.workforcemgmt.service;

import com.railse.workforcemgmt.dto.AddCommentRequest;
import com.railse.workforcemgmt.dto.CreateTaskRequest;
import com.railse.workforcemgmt.dto.TaskActivityDto;
import com.railse.workforcemgmt.dto.TaskCommentDto;
import com.railse.workforcemgmt.dto.TaskDto;
import com.railse.workforcemgmt.entity.Priority;
import com.railse.workforcemgmt.exception.ResourceNotFoundException;
import com.railse.workforcemgmt.mapper.TaskActivityMapper;
import com.railse.workforcemgmt.mapper.TaskCommentMapper;
import com.railse.workforcemgmt.mapper.TaskMapper;
import com.railse.workforcemgmt.model.Task;
import com.railse.workforcemgmt.model.TaskComment;
import com.railse.workforcemgmt.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.argThat; // Corrected: Removed duplicate import

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private TaskActivityService taskActivityService;

    @Mock
    private TaskCommentService taskCommentService;

    @Mock
    private TaskActivityMapper taskActivityMapper;

    @Mock
    private TaskCommentMapper taskCommentMapper;

    @InjectMocks
    private TaskService taskService;

    private Task task1;
    private Task task2;
    private TaskDto taskDto1;
    private TaskDto taskDto2;

    @BeforeEach
    void setUp() {
        // Initialize Task objects with mutable lists for activityLog and comments
        task1 = new Task(1L, "Task 1", "Desc 1", "Alice", LocalDateTime.now(), LocalDateTime.now().plusDays(1), Priority.HIGH, false, LocalDateTime.now(), LocalDateTime.now(), new ArrayList<>(), new ArrayList<>());
        task2 = new Task(2L, "Task 2", "Desc 2", "Bob", LocalDateTime.now(), LocalDateTime.now().plusDays(2), Priority.MEDIUM, true, LocalDateTime.now(), LocalDateTime.now(), new ArrayList<>(), new ArrayList<>());

        taskDto1 = new TaskDto();
        taskDto1.setId(1L);
        taskDto1.setTitle("Task 1");
        taskDto1.setDescription("Desc 1");
        taskDto1.setAssignee("Alice");
        taskDto1.setStartDate(LocalDateTime.now());
        taskDto1.setDueDate(LocalDateTime.now().plusDays(1));
        taskDto1.setPriority(Priority.HIGH);
        taskDto1.setCancelled(false);
        // Initialize DTO lists with mutable ArrayLists
        taskDto1.setActivityLog(new ArrayList<>());
        taskDto1.setComments(new ArrayList<>());

        taskDto2 = new TaskDto();
        taskDto2.setId(2L);
        taskDto2.setTitle("Task 2");
        taskDto2.setDescription("Desc 2");
        taskDto2.setAssignee("Bob");
        taskDto2.setStartDate(LocalDateTime.now());
        taskDto2.setDueDate(LocalDateTime.now().plusDays(2));
        taskDto2.setPriority(Priority.MEDIUM);
        taskDto2.setCancelled(true);
        // Initialize DTO lists with mutable ArrayLists
        taskDto2.setActivityLog(new ArrayList<>());
        taskDto2.setComments(new ArrayList<>());
    }

    @Test
    void getAllTasks_shouldReturnListOfTaskDtos() {
        List<Task> tasks = Arrays.asList(task1, task2);
        when(taskRepository.findAll()).thenReturn(tasks);
        when(taskMapper.toDto(task1)).thenReturn(taskDto1);
        when(taskMapper.toDto(task2)).thenReturn(taskDto2);

        List<TaskDto> result = taskService.getAllTasks();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Task 1", result.get(0).getTitle());
        assertEquals("Task 2", result.get(1).getTitle());
        verify(taskRepository, times(1)).findAll();
        verify(taskMapper, times(2)).toDto(any(Task.class));
    }

    @Test
    void getActiveTasks_shouldReturnOnlyNonCancelledTasks() {
        Task activeTask = new Task(3L, "Active Task", "Active Desc", "Charlie", LocalDateTime.now(), LocalDateTime.now().plusDays(3), Priority.LOW, false, LocalDateTime.now(), LocalDateTime.now(), new ArrayList<>(), new ArrayList<>());
        TaskDto activeTaskDto = new TaskDto();
        activeTaskDto.setId(3L);
        activeTaskDto.setTitle("Active Task");
        activeTaskDto.setCancelled(false);
        activeTaskDto.setStartDate(LocalDateTime.now());
        activeTaskDto.setDueDate(LocalDateTime.now().plusDays(3));
        activeTaskDto.setPriority(Priority.LOW);

        List<Task> activeTasks = Arrays.asList(activeTask);
        when(taskRepository.findByCancelledFalse()).thenReturn(activeTasks);
        when(taskMapper.toDto(activeTask)).thenReturn(activeTaskDto);

        List<TaskDto> result = taskService.getActiveTasks();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Active Task", result.get(0).getTitle());
        assertFalse(result.get(0).isCancelled());
        verify(taskRepository, times(1)).findByCancelledFalse();
        verify(taskMapper, times(1)).toDto(any(Task.class));
    }

    @Test
    void getTasksForDateRange_shouldReturnFilteredTasks() {
        LocalDateTime startDate = LocalDateTime.of(2025, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 1, 31, 23, 59);

        // Task within range and not cancelled (should be included)
        Task taskInRange = new Task(
            1L, "Task in Range", "Description", "A",
            LocalDateTime.of(2025, 1, 15, 0, 0),
            LocalDateTime.of(2025, 1, 20, 0, 0),
            Priority.MEDIUM, false,
            LocalDateTime.now(), LocalDateTime.now(),
            new ArrayList<>(), new ArrayList<>()
        );

        // Cancelled task in range (should be excluded)
        Task cancelledTask = new Task(
            2L, "Cancelled Task", "Description", "B",
            LocalDateTime.of(2025, 1, 15, 0, 0),
            LocalDateTime.of(2025, 1, 20, 0, 0),
            Priority.HIGH, true,
            LocalDateTime.now(), LocalDateTime.now(),
            new ArrayList<>(), new ArrayList<>()
        );

        // Task out of range (should be excluded)
        Task outOfRangeTask = new Task(
            3L, "Out of Range", "Description", "C",
            LocalDateTime.of(2025, 2, 5, 0, 0),
            LocalDateTime.of(2025, 2, 10, 0, 0),
            Priority.LOW, false,
            LocalDateTime.now(), LocalDateTime.now(),
            new ArrayList<>(), new ArrayList<>()
        );

        // Only include valid tasks in the mocked return
        List<Task> tasksInRange = Collections.singletonList(taskInRange);

        TaskDto taskDtoInRange = new TaskDto();
        taskDtoInRange.setId(1L);
        taskDtoInRange.setTitle("Task in Range");
        taskDtoInRange.setStartDate(taskInRange.getStartDate());
        taskDtoInRange.setDueDate(taskInRange.getDueDate());
        taskDtoInRange.setCancelled(false);

        when(taskRepository.findActiveTasksForDateRange(startDate, endDate)).thenReturn(tasksInRange);
        when(taskMapper.toDto(taskInRange)).thenReturn(taskDtoInRange);

        List<TaskDto> result = taskService.getTasksForDateRange(startDate, endDate);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Task in Range", result.get(0).getTitle());

        verify(taskRepository, times(1)).findActiveTasksForDateRange(startDate, endDate);
        verify(taskMapper, times(1)).toDto(taskInRange);
    }

    @Test
    void getTaskByIdWithDetails_shouldReturnTaskDtoWithDetailsWhenFound() {
        Task taskWithDetails = new Task(1L, "Task with Details", "Desc", "Alice", LocalDateTime.now(), LocalDateTime.now().plusDays(1), Priority.HIGH, false, LocalDateTime.now(), LocalDateTime.now(), new ArrayList<>(), new ArrayList<>());
        TaskComment comment = new TaskComment(taskWithDetails, "User", "This is a comment.");
        taskWithDetails.addComment(comment);

        TaskDto expectedDto = new TaskDto();
        expectedDto.setId(1L);
        expectedDto.setTitle("Task with Details");
        expectedDto.setComments(Arrays.asList(new TaskCommentDto()));
        expectedDto.setActivityLog(Arrays.asList(new TaskActivityDto()));

        when(taskRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(taskWithDetails));
        when(taskMapper.toDto(taskWithDetails)).thenReturn(expectedDto);
        when(taskCommentMapper.toDtoList(anyList())).thenReturn(expectedDto.getComments());
        when(taskActivityMapper.toDtoList(anyList())).thenReturn(expectedDto.getActivityLog());

        TaskDto result = taskService.getTaskByIdWithDetails(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Task with Details", result.getTitle());
        assertNotNull(result.getComments());
        assertNotNull(result.getActivityLog());
        verify(taskRepository, times(1)).findByIdWithDetails(1L);
        verify(taskMapper, times(1)).toDto(taskWithDetails);
        verify(taskCommentMapper, times(1)).toDtoList(anyList());
        verify(taskActivityMapper, times(1)).toDtoList(anyList());
    }

    @Test
    void getTaskById_shouldReturnTaskDtoWhenFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task1));
        when(taskMapper.toDto(task1)).thenReturn(taskDto1);

        TaskDto result = taskService.getTaskById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Task 1", result.getTitle());
        verify(taskRepository, times(1)).findById(1L);
        verify(taskMapper, times(1)).toDto(task1);
    }

    @Test
    void getTaskById_shouldThrowResourceNotFoundExceptionWhenNotFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.getTaskById(99L));
        verify(taskRepository, times(1)).findById(99L);
        verify(taskMapper, never()).toDto(any(Task.class));
    }

    @Test
    void createTask_shouldReturnCreatedTaskDtoAndLogActivity() {
        CreateTaskRequest createTaskRequest = new CreateTaskRequest();
        createTaskRequest.setTitle("New Task");
        createTaskRequest.setDescription("New Description");
        createTaskRequest.setAssignee("Test User");
        createTaskRequest.setDueDate(LocalDateTime.now().plusDays(5));
        createTaskRequest.setPriority(Priority.URGENT);
        createTaskRequest.setStartDate(LocalDateTime.now());

        Task taskToSave = new Task();
        taskToSave.setTitle("New Task");
        taskToSave.setDescription("New Description");
        taskToSave.setAssignee("Test User");
        taskToSave.setDueDate(LocalDateTime.now().plusDays(5));
        taskToSave.setPriority(Priority.URGENT);
        taskToSave.setStartDate(LocalDateTime.now());
        taskToSave.setCancelled(false);

        Task savedTask = new Task(3L, "New Task", null, "Test User", LocalDateTime.now(), LocalDateTime.now().plusDays(5), Priority.URGENT, false, LocalDateTime.now(), LocalDateTime.now(), new ArrayList<>(), new ArrayList<>());
        TaskDto expectedDto = new TaskDto();
        expectedDto.setId(3L);
        expectedDto.setTitle("New Task");
        expectedDto.setAssignee("Test User");
        expectedDto.setPriority(Priority.URGENT);

        when(taskMapper.toEntity(createTaskRequest)).thenReturn(taskToSave);
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);
        when(taskMapper.toDto(savedTask)).thenReturn(expectedDto);
        when(taskActivityService.logActivity(any(Task.class), anyString())).thenReturn(null);

        TaskDto result = taskService.createTask(createTaskRequest);

        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals("New Task", result.getTitle());
        assertEquals("Test User", result.getAssignee());
        assertEquals(Priority.URGENT, result.getPriority());
        verify(taskMapper, times(1)).toEntity(createTaskRequest);
        verify(taskRepository, times(1)).save(taskToSave);
        verify(taskMapper, times(1)).toDto(savedTask);
        verify(taskActivityService, times(1)).logActivity(eq(savedTask), anyString());
    }

    @Test
    void updateTask_shouldReturnUpdatedTaskDtoAndLogActivity() {
        TaskDto updateDetails = new TaskDto();
        updateDetails.setTitle("Updated Title");
        updateDetails.setDescription("Updated Desc");
        updateDetails.setPriority(Priority.LOW);
        updateDetails.setCancelled(true);
        updateDetails.setStartDate(LocalDateTime.now().plusDays(1));
        updateDetails.setDueDate(LocalDateTime.now().plusDays(10));

        Task existingTask = new Task(1L, "Old Title", "Old Desc", "Old Assignee", LocalDateTime.now(), LocalDateTime.now().plusDays(10), Priority.MEDIUM, false, LocalDateTime.now(), LocalDateTime.now(), new ArrayList<>(), new ArrayList<>());
        Task taskAfterUpdate = new Task(1L, "Updated Title", "Updated Desc", "Old Assignee", LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(10), Priority.LOW, true, LocalDateTime.now(), LocalDateTime.now(), new ArrayList<>(), new ArrayList<>());
        TaskDto expectedDto = new TaskDto();
        expectedDto.setId(1L);
        expectedDto.setTitle("Updated Title");
        expectedDto.setDescription("Updated Desc");
        expectedDto.setAssignee("Old Assignee");
        expectedDto.setPriority(Priority.LOW);
        expectedDto.setCancelled(true);
        expectedDto.setStartDate(LocalDateTime.now().plusDays(1));
        expectedDto.setDueDate(LocalDateTime.now().plusDays(10));

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenReturn(taskAfterUpdate);
        when(taskMapper.toDto(taskAfterUpdate)).thenReturn(expectedDto);
        when(taskActivityService.logActivity(any(Task.class), anyString())).thenReturn(null);

        TaskDto result = taskService.updateTask(1L, updateDetails);

        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        assertEquals(Priority.LOW, result.getPriority());
        assertTrue(result.isCancelled());

        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).save(argThat(task ->
                task.getTitle().equals("Updated Title") &&
                task.getAssignee().equals("Old Assignee") &&
                task.getPriority().equals(Priority.LOW) &&
                task.isCancelled() == true
        ));
        verify(taskMapper, times(1)).toDto(taskAfterUpdate);
        verify(taskActivityService, times(2)).logActivity(eq(taskAfterUpdate), anyString());
    }

    @Test
    void updateTask_shouldThrowResourceNotFoundExceptionWhenTaskNotFound() {
        TaskDto updateDetails = new TaskDto();
        updateDetails.setTitle("Non Existent");
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.updateTask(99L, updateDetails));
        verify(taskRepository, times(1)).findById(99L);
        verify(taskRepository, never()).save(any(Task.class));
        verify(taskMapper, never()).toDto(any(Task.class));
    }

    @Test
    void reassignTask_shouldCancelOldTaskAndCreateNewTaskAndLogActivity() {
        String newAssignee = "New Assignee";
        Task originalTask = new Task(1L, "Original Task", "Desc", "Old Assignee", LocalDateTime.now(), LocalDateTime.now().plusDays(5), Priority.MEDIUM, false, LocalDateTime.now(), LocalDateTime.now(), new ArrayList<>(), new ArrayList<>());
        Task cancelledOriginalTask = new Task(1L, "Original Task", "Desc", "Old Assignee", LocalDateTime.now(), LocalDateTime.now().plusDays(5), Priority.MEDIUM, true, LocalDateTime.now(), LocalDateTime.now(), new ArrayList<>(), new ArrayList<>());
        Task newTaskForAssignee = new Task(null, "Original Task (Reassigned)", "Desc", newAssignee, LocalDateTime.now(), LocalDateTime.now().plusDays(5), Priority.MEDIUM, false, null, null, new ArrayList<>(), new ArrayList<>());
        Task savedNewTask = new Task(2L, "Original Task (Reassigned)", "Desc", newAssignee, LocalDateTime.now(), LocalDateTime.now().plusDays(5), Priority.MEDIUM, false, LocalDateTime.now(), LocalDateTime.now(), new ArrayList<>(), new ArrayList<>());
        TaskDto expectedDto = new TaskDto();
        expectedDto.setId(2L);
        expectedDto.setTitle("Original Task (Reassigned)");
        expectedDto.setAssignee(newAssignee);
        expectedDto.setCancelled(false);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(originalTask));
        when(taskMapper.copyForReassignment(originalTask)).thenReturn(newTaskForAssignee);
        when(taskRepository.save(originalTask)).thenReturn(cancelledOriginalTask);
        when(taskRepository.save(newTaskForAssignee)).thenReturn(savedNewTask);
        when(taskMapper.toDto(savedNewTask)).thenReturn(expectedDto);
        when(taskActivityService.logActivity(any(Task.class), anyString())).thenReturn(null);

        TaskDto result = taskService.reassignTask(1L, newAssignee);

        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals(newAssignee, result.getAssignee());
        assertFalse(result.isCancelled());

        verify(taskRepository, times(1)).findById(1L);
        verify(taskMapper, times(1)).copyForReassignment(originalTask);
        verify(taskRepository, times(1)).save(originalTask);
        verify(taskRepository, times(1)).save(newTaskForAssignee);
        verify(taskMapper, times(1)).toDto(savedNewTask);
        verify(taskActivityService, times(2)).logActivity(any(Task.class), anyString());
    }

    @Test
    void reassignTask_shouldThrowExceptionIfOriginalTaskNotFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> taskService.reassignTask(99L, "New Assignee"));
    }

    @Test
    void reassignTask_shouldThrowExceptionIfTaskAlreadyCancelled() {
        task1.setCancelled(true);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task1));
        assertThrows(IllegalStateException.class, () -> taskService.reassignTask(1L, "New Assignee"));
    }

    @Test
    void reassignTask_shouldThrowExceptionIfSameAssignee() {
        task1.setAssignee("Alice");
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task1));
        assertThrows(IllegalArgumentException.class, () -> taskService.reassignTask(1L, "Alice"));
    }

    @Test
    void updateTaskPriority_shouldUpdatePriorityAndLogActivity() {
        Priority newPriority = Priority.URGENT;
        Task existingTask = new Task(1L, "Task 1", "Desc", "Alice", LocalDateTime.now(), LocalDateTime.now().plusDays(1), Priority.HIGH, false, LocalDateTime.now(), LocalDateTime.now(), new ArrayList<>(), new ArrayList<>());
        Task updatedTask = new Task(1L, "Task 1", "Desc", "Alice", LocalDateTime.now(), LocalDateTime.now().plusDays(1), newPriority, false, LocalDateTime.now(), LocalDateTime.now(), new ArrayList<>(), new ArrayList<>());
        TaskDto expectedDto = new TaskDto();
        expectedDto.setId(1L);
        expectedDto.setTitle("Task 1");
        expectedDto.setPriority(newPriority);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);
        when(taskMapper.toDto(updatedTask)).thenReturn(expectedDto);
        when(taskActivityService.logActivity(any(Task.class), anyString())).thenReturn(null);

        TaskDto result = taskService.updateTaskPriority(1L, newPriority);

        assertNotNull(result);
        assertEquals(newPriority, result.getPriority());
        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).save(argThat(task -> task.getPriority().equals(newPriority)));
        verify(taskMapper, times(1)).toDto(updatedTask);
        verify(taskActivityService, times(1)).logActivity(eq(updatedTask), anyString());
    }

    @Test
    void updateTaskPriority_shouldThrowExceptionIfPriorityIsSame() {
        Priority currentPriority = Priority.HIGH;
        task1.setPriority(currentPriority);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task1));
        assertThrows(IllegalArgumentException.class, () -> taskService.updateTaskPriority(1L, currentPriority));
    }

    @Test
    void updateTaskPriority_shouldThrowResourceNotFoundExceptionWhenTaskNotFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> taskService.updateTaskPriority(99L, Priority.LOW));
    }

    @Test
    void deleteTask_shouldDeleteTaskSuccessfullyAndLogActivity() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task1));
        doNothing().when(taskRepository).delete(task1);
        when(taskActivityService.logActivity(any(Task.class), anyString())).thenReturn(null);

        taskService.deleteTask(1L);

        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).delete(task1);
        verify(taskActivityService, times(1)).logActivity(eq(task1), anyString());
    }

    @Test
    void deleteTask_shouldThrowResourceNotFoundExceptionWhenTaskNotFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.deleteTask(99L));
        verify(taskRepository, times(1)).findById(99L);
        verify(taskRepository, never()).delete(any(Task.class));
    }

    @Test
    void addComment_shouldReturnCreatedCommentDto() {
        Long taskId = 1L;
        AddCommentRequest request = new AddCommentRequest();
        request.setAuthor("Test User");
        request.setComment("This is a test comment.");

        TaskCommentDto expectedDto = new TaskCommentDto();
        expectedDto.setId(10L);
        expectedDto.setAuthor("Test User");
        expectedDto.setComment("This is a test comment.");
        expectedDto.setTimestamp(LocalDateTime.now());

        when(taskCommentService.addCommentToTask(eq(taskId), any(AddCommentRequest.class)))
                .thenReturn(expectedDto);

        TaskCommentDto result = taskService.addComment(taskId, request);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("Test User", result.getAuthor());
        assertEquals("This is a test comment.", result.getComment());
        verify(taskCommentService, times(1)).addCommentToTask(eq(taskId), any(AddCommentRequest.class));
    }
}
