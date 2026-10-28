package com.isma.taskmanager.dto;

import java.time.Instant;

/**
 * Outgoing representation of a project.
 */
public record ProjectResponse(Long id, String name, String description, Instant createdAt) {
}
