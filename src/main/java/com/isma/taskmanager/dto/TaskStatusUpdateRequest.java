package com.isma.taskmanager.dto;

import com.isma.taskmanager.domain.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * Incoming payload for changing a task's status.
 */
public record TaskStatusUpdateRequest(
        @NotNull
        @Schema(description = "New task status", example = "DONE")
        TaskStatus status) {
}
