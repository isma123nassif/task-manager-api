package com.isma.taskmanager.controller;

import com.isma.taskmanager.domain.TaskPriority;
import com.isma.taskmanager.domain.TaskStatus;
import com.isma.taskmanager.dto.TaskRequest;
import com.isma.taskmanager.dto.TaskResponse;
import com.isma.taskmanager.dto.TaskStatusUpdateRequest;
import com.isma.taskmanager.exception.GlobalExceptionHandler;
import com.isma.taskmanager.exception.ResourceNotFoundException;
import com.isma.taskmanager.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
@Import(GlobalExceptionHandler.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @Test
    void create_shouldReturn201WithLocation() throws Exception {
        TaskResponse response = new TaskResponse(
                1L, "Write report", "Annual report", TaskStatus.TODO, TaskPriority.HIGH, null, 1L, "My Project",
                Instant.parse("2026-01-01T00:00:00Z"), Instant.parse("2026-01-01T00:00:00Z"));
        when(taskService.create(any(TaskRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Write report","description":"Annual report","status":"TODO","priority":"HIGH","projectId":1}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/v1/tasks/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Write report"));
    }

    @Test
    void create_shouldReturn400WhenTitleBlank() throws Exception {
        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"","description":"Annual report","projectId":1}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_shouldReturn400WhenProjectIdMissing() throws Exception {
        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Write report","description":"Annual report"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findById_shouldReturn404WhenNotFound() throws Exception {
        when(taskService.findById(99L))
                .thenThrow(new ResourceNotFoundException("Task with id 99 not found"));

        mockMvc.perform(get("/api/v1/tasks/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateStatus_shouldReturn200() throws Exception {
        TaskResponse response = new TaskResponse(
                1L, "Write report", "Annual report", TaskStatus.DONE, TaskPriority.HIGH, null, 1L, "My Project",
                Instant.parse("2026-01-01T00:00:00Z"), Instant.parse("2026-01-01T00:00:00Z"));
        when(taskService.updateStatus(eq(1L), any(TaskStatusUpdateRequest.class))).thenReturn(response);

        mockMvc.perform(patch("/api/v1/tasks/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"status":"DONE"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DONE"));
    }

    @Test
    void findAll_shouldPassFiltersToService() throws Exception {
        when(taskService.findAll(any(), any(), any(), any())).thenReturn(Page.empty());

        mockMvc.perform(get("/api/v1/tasks")
                        .param("status", "TODO")
                        .param("priority", "HIGH")
                        .param("projectId", "1"))
                .andExpect(status().isOk());

        verify(taskService).findAll(eq(TaskStatus.TODO), eq(TaskPriority.HIGH), eq(1L), any(Pageable.class));
    }
}
