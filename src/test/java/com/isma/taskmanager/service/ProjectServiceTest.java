package com.isma.taskmanager.service;

import com.isma.taskmanager.domain.Project;
import com.isma.taskmanager.dto.ProjectRequest;
import com.isma.taskmanager.dto.ProjectResponse;
import com.isma.taskmanager.exception.DuplicateResourceException;
import com.isma.taskmanager.exception.ResourceNotFoundException;
import com.isma.taskmanager.mapper.ProjectMapper;
import com.isma.taskmanager.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMapper projectMapper;

    @InjectMocks
    private ProjectService projectService;

    @Test
    void create_shouldReturnResponse() {
        ProjectRequest request = new ProjectRequest("My Project", "Description");
        Project project = new Project("My Project", "Description");
        project.setId(1L);
        project.setCreatedAt(Instant.parse("2026-01-01T00:00:00Z"));
        ProjectResponse response = new ProjectResponse(1L, "My Project", "Description", project.getCreatedAt());

        when(projectRepository.existsByNameIgnoreCase("My Project")).thenReturn(false);
        when(projectMapper.toEntity(request)).thenReturn(project);
        when(projectRepository.saveAndFlush(project)).thenReturn(project);
        when(projectMapper.toResponse(project)).thenReturn(response);

        ProjectResponse result = projectService.create(request);

        assertThat(result).isEqualTo(response);
    }

    @Test
    void create_shouldThrowWhenNameAlreadyExists() {
        ProjectRequest request = new ProjectRequest("Duplicate", null);

        when(projectRepository.existsByNameIgnoreCase("Duplicate")).thenReturn(true);

        assertThatThrownBy(() -> projectService.create(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Duplicate");

        verify(projectRepository, never()).saveAndFlush(any());
    }

    @Test
    void findById_shouldThrowWhenNotFound() {
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update_shouldThrowWhenNameBelongsToAnotherProject() {
        ProjectRequest request = new ProjectRequest("Taken", null);
        Project existing = new Project("Old", null);
        existing.setId(1L);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(projectRepository.existsByNameIgnoreCaseAndIdNot("Taken", 1L)).thenReturn(true);

        assertThatThrownBy(() -> projectService.update(1L, request))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void delete_shouldThrowWhenNotFound() {
        when(projectRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> projectService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
