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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskService taskService;

    @Test
    void create_shouldReturnResponse() {
        Project project = new Project("My Project", null);
        project.setId(1L);
        TaskRequest request = new TaskRequest("Write report", "Annual report", TaskStatus.TODO, TaskPriority.HIGH, null, 1L);
        Task task = new Task("Write report", "Annual report", TaskStatus.TODO, TaskPriority.HIGH, null, project);
        task.setId(1L);
        TaskResponse response = new TaskResponse(
                1L, "Write report", "Annual report", TaskStatus.TODO, TaskPriority.HIGH, null, 1L, "My Project", null, null);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(taskMapper.toEntity(request, project)).thenReturn(task);
        when(taskRepository.saveAndFlush(task)).thenReturn(task);
        when(taskMapper.toResponse(task)).thenReturn(response);

        TaskResponse result = taskService.create(request);

        assertThat(result).isEqualTo(response);
    }

    @Test
    void create_shouldThrowWhenProjectMissing() {
        TaskRequest request = new TaskRequest("Write report", null, null, null, null, 99L);

        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.create(request))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(taskRepository, never()).saveAndFlush(any());
    }

    @Test
    void updateStatus_shouldReturnResponse() {
        Project project = new Project("My Project", null);
        project.setId(1L);
        Task task = new Task("Write report", null, TaskStatus.TODO, TaskPriority.MEDIUM, null, project);
        task.setId(1L);
        TaskResponse response = new TaskResponse(
                1L, "Write report", null, TaskStatus.DONE, TaskPriority.MEDIUM, null, 1L, "My Project", null, null);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskMapper.toResponse(task)).thenReturn(response);

        TaskResponse result = taskService.updateStatus(1L, new TaskStatusUpdateRequest(TaskStatus.DONE));

        assertThat(result).isEqualTo(response);
        assertThat(task.getStatus()).isEqualTo(TaskStatus.DONE);
    }

    @Test
    void update_shouldThrowWhenNotFound() {
        TaskRequest request = new TaskRequest("Write report", null, TaskStatus.TODO, TaskPriority.MEDIUM, null, 1L);

        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.update(99L, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_shouldThrowWhenNotFound() {
        when(taskRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> taskService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
