package com.isma.taskmanager.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Incoming payload for creating or updating a project.
 */
public record ProjectRequest(
        @NotBlank @Size(max = 100)
        @Schema(description = "Project name", example = "Web Panadería Martínez")
        String name,
        @Size(max = 1000)
        @Schema(description = "Project description", example = "Online store for a family bakery")
        String description) {
}
