package com.isma.taskmanager.dto;

import com.isma.taskmanager.domain.TaskPriority;
import com.isma.taskmanager.domain.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Outgoing representation of a task.
 */
public record TaskResponse(
        @Schema(description = "Task identifier", example = "1")
        Long id,
        @Schema(description = "Task title", example = "Design home page")
        String title,
        @Schema(description = "Task description", example = "Create the landing page mockup")
        String description,
        @Schema(description = "Task status", example = "IN_PROGRESS")
        TaskStatus status,
        @Schema(description = "Task priority", example = "HIGH")
        TaskPriority priority,
        @Schema(description = "Due date", example = "2026-11-10")
        LocalDate dueDate,
        @Schema(description = "Identifier of the owning project", example = "1")
        Long projectId,
        @Schema(description = "Name of the owning project", example = "Web Panadería Martínez")
        String projectName,
        @Schema(description = "Creation timestamp (UTC)", example = "2026-10-28T10:15:30Z")
        Instant createdAt,
        @Schema(description = "Last update timestamp (UTC)", example = "2026-10-29T08:00:00Z")
        Instant updatedAt) {
}
