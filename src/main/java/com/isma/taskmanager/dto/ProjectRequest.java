package com.isma.taskmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Incoming payload for creating or updating a project.
 */
public record ProjectRequest(
        @NotBlank @Size(max = 100) String name,
        @Size(max = 1000) String description) {
}
