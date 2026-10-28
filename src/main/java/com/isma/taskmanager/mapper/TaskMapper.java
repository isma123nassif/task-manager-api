package com.isma.taskmanager.mapper;

import com.isma.taskmanager.domain.Project;
import com.isma.taskmanager.domain.Task;
import com.isma.taskmanager.domain.TaskPriority;
import com.isma.taskmanager.domain.TaskStatus;
import com.isma.taskmanager.dto.TaskRequest;
import com.isma.taskmanager.dto.TaskResponse;
import org.springframework.stereotype.Component;

/**
 * Maps between {@link Task} entities and their DTOs.
 */
@Component
public class TaskMapper {

    public Task toEntity(TaskRequest request, Project project) {
        return new Task(
                request.title(),
                request.description(),
                defaultStatus(request.status()),
                defaultPriority(request.priority()),
                request.dueDate(),
                project);
    }

    public TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getDueDate(),
                task.getProject().getId(),
                task.getProject().getName(),
                task.getCreatedAt(),
                task.getUpdatedAt());
    }

    public void updateEntity(Task task, TaskRequest request, Project project) {
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setStatus(defaultStatus(request.status()));
        task.setPriority(defaultPriority(request.priority()));
        task.setDueDate(request.dueDate());
        task.setProject(project);
    }

    private TaskStatus defaultStatus(TaskStatus status) {
        return status == null ? TaskStatus.TODO : status;
    }

    private TaskPriority defaultPriority(TaskPriority priority) {
        return priority == null ? TaskPriority.MEDIUM : priority;
    }
}
