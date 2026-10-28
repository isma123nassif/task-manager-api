package com.isma.taskmanager.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/**
 * Outgoing representation of a project.
 */
public record ProjectResponse(
        @Schema(description = "Project identifier", example = "1")
        Long id,
        @Schema(description = "Project name", example = "Web Panadería Martínez")
        String name,
        @Schema(description = "Project description", example = "Online store for a family bakery")
        String description,
        @Schema(description = "Creation timestamp (UTC)", example = "2026-10-28T10:15:30Z")
        Instant createdAt) {
}
