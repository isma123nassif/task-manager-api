package com.isma.taskmanager.dto;

import com.isma.taskmanager.domain.TaskStatus;
import jakarta.validation.constraints.NotNull;

/**
 * Incoming payload for changing a task's status.
 */
public record TaskStatusUpdateRequest(@NotNull TaskStatus status) {
}
