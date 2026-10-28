package com.isma.taskmanager.dto;

import com.isma.taskmanager.domain.TaskPriority;
import com.isma.taskmanager.domain.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Incoming payload for creating or updating a task.
 */
public record TaskRequest(
        @NotBlank @Size(max = 200)
        @Schema(description = "Task title", example = "Design home page")
        String title,
        @Size(max = 2000)
        @Schema(description = "Task description", example = "Create the landing page mockup")
        String description,
        @Schema(description = "Task status", example = "TODO")
        TaskStatus status,
        @Schema(description = "Task priority", example = "HIGH")
        TaskPriority priority,
        @Schema(description = "Due date", example = "2026-11-10")
        LocalDate dueDate,
        @NotNull
        @Schema(description = "Identifier of the owning project", example = "1")
        Long projectId) {
}
