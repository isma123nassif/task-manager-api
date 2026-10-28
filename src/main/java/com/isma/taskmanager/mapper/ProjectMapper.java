package com.isma.taskmanager.mapper;

import com.isma.taskmanager.domain.Project;
import com.isma.taskmanager.dto.ProjectRequest;
import com.isma.taskmanager.dto.ProjectResponse;
import org.springframework.stereotype.Component;

/**
 * Maps between {@link Project} entities and their DTOs.
 */
@Component
public class ProjectMapper {

    public Project toEntity(ProjectRequest request) {
        return new Project(request.name(), request.description());
    }

    public ProjectResponse toResponse(Project project) {
        return new ProjectResponse(project.getId(), project.getName(), project.getDescription(), project.getCreatedAt());
    }

    public void updateEntity(Project project, ProjectRequest request) {
        project.setName(request.name());
        project.setDescription(request.description());
    }
}
