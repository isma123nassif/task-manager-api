package com.isma.taskmanager.service;

import com.isma.taskmanager.domain.Project;
import com.isma.taskmanager.dto.ProjectRequest;
import com.isma.taskmanager.dto.ProjectResponse;
import com.isma.taskmanager.exception.DuplicateResourceException;
import com.isma.taskmanager.exception.ResourceNotFoundException;
import com.isma.taskmanager.mapper.ProjectMapper;
import com.isma.taskmanager.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Business logic for managing projects.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    @Transactional
    public ProjectResponse create(ProjectRequest request) {
        ensureNameAvailable(request.name(), null);
        Project project = projectMapper.toEntity(request);
        return projectMapper.toResponse(projectRepository.saveAndFlush(project));
    }

    public ProjectResponse findById(Long id) {
        return projectRepository.findById(id)
                .map(projectMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Project with id " + id + " not found"));
    }

    public Page<ProjectResponse> findAll(Pageable pageable) {
        return projectRepository.findAll(pageable).map(projectMapper::toResponse);
    }

    @Transactional
    public ProjectResponse update(Long id, ProjectRequest request) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project with id " + id + " not found"));
        ensureNameAvailable(request.name(), id);
        projectMapper.updateEntity(project, request);
        projectRepository.flush();
        return projectMapper.toResponse(project);
    }

    @Transactional
    public void delete(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Project with id " + id + " not found");
        }
        projectRepository.deleteById(id);
    }

    private void ensureNameAvailable(String name, Long id) {
        boolean exists = id == null
                ? projectRepository.existsByNameIgnoreCase(name)
                : projectRepository.existsByNameIgnoreCaseAndIdNot(name, id);
        if (exists) {
            throw new DuplicateResourceException("A project with name '" + name + "' already exists");
        }
    }
}
