// src/main/java/com/railse/workforcemgmt/service/TaskActivityService.java
package com.railse.workforcemgmt.service;

import com.railse.workforcemgmt.model.Task;
import com.railse.workforcemgmt.model.TaskActivity;
import com.railse.workforcemgmt.repository.TaskActivityRepository;
import org.springframework.stereotype.Service;
//import java.time.LocalDateTime;

@Service
public class TaskActivityService {

    private final TaskActivityRepository taskActivityRepository;

    public TaskActivityService(TaskActivityRepository taskActivityRepository) {
        this.taskActivityRepository = taskActivityRepository;
    }

    public TaskActivity logActivity(Task task, String message) {
        TaskActivity activity = new TaskActivity(task, message);
        task.addActivity(activity);
        return taskActivityRepository.save(activity);
    }
}
