package com.isma.taskmanager.service;

import com.isma.taskmanager.domain.Project;
import com.isma.taskmanager.domain.Task;
import com.isma.taskmanager.domain.TaskPriority;
import com.isma.taskmanager.domain.TaskStatus;
import com.isma.taskmanager.dto.TaskRequest;
import com.isma.taskmanager.dto.TaskResponse;
import com.isma.taskmanager.dto.TaskStatusUpdateRequest;
import com.isma.taskmanager.exception.ResourceNotFoundException;
import com.isma.taskmanager.mapper.TaskMapper;
import com.isma.taskmanager.repository.ProjectRepository;
import com.isma.taskmanager.repository.TaskRepository;
import com.isma.taskmanager.repository.TaskSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Business logic for managing tasks.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final TaskMapper taskMapper;

    @Transactional
    public TaskResponse create(TaskRequest request) {
        Project project = projectRepository.findById(request.projectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project with id " + request.projectId() + " not found"));
        Task task = taskMapper.toEntity(request, project);
        return taskMapper.toResponse(taskRepository.saveAndFlush(task));
    }

    public TaskResponse findById(Long id) {
        return taskRepository.findById(id)
                .map(taskMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found"));
    }

    public Page<TaskResponse> findAll(TaskStatus status, TaskPriority priority, Long projectId, Pageable pageable) {
        Specification<Task> spec = TaskSpecifications.withFilters(status, priority, projectId);
        return taskRepository.findAll(spec, pageable).map(taskMapper::toResponse);
    }

    @Transactional
    public TaskResponse update(Long id, TaskRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found"));
        Project project = projectRepository.findById(request.projectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project with id " + request.projectId() + " not found"));
        taskMapper.updateEntity(task, request, project);
        taskRepository.flush();
        return taskMapper.toResponse(task);
    }

    @Transactional
    public TaskResponse updateStatus(Long id, TaskStatusUpdateRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found"));
        task.setStatus(request.status());
        taskRepository.flush();
        return taskMapper.toResponse(task);
    }

    @Transactional
    public void delete(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new ResourceNotFoundException("Task with id " + id + " not found");
        }
        taskRepository.deleteById(id);
    }
}
