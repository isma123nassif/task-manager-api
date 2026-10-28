package com.isma.taskmanager.dto;

import com.isma.taskmanager.domain.TaskPriority;
import com.isma.taskmanager.domain.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Incoming payload for creating or updating a task.
 */
public record TaskRequest(
        @NotBlank @Size(max = 200) String title,
        @Size(max = 2000) String description,
        TaskStatus status,
        TaskPriority priority,
        LocalDate dueDate,
        @NotNull Long projectId) {
}
