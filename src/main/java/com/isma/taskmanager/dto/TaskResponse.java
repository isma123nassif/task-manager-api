package com.isma.taskmanager.dto;

import com.isma.taskmanager.domain.TaskPriority;
import com.isma.taskmanager.domain.TaskStatus;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Outgoing representation of a task.
 */
public record TaskResponse(
        Long id,
        String title,
        String description,
        TaskStatus status,
        TaskPriority priority,
        LocalDate dueDate,
        Long projectId,
        String projectName,
        Instant createdAt,
        Instant updatedAt) {
}
