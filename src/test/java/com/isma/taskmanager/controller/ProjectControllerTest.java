package com.isma.taskmanager.controller;

import com.isma.taskmanager.dto.ProjectRequest;
import com.isma.taskmanager.dto.ProjectResponse;
import com.isma.taskmanager.exception.DuplicateResourceException;
import com.isma.taskmanager.exception.GlobalExceptionHandler;
import com.isma.taskmanager.exception.ResourceNotFoundException;
import com.isma.taskmanager.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectController.class)
@Import(GlobalExceptionHandler.class)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProjectService projectService;

    @Test
    void create_shouldReturn201WithLocation() throws Exception {
        ProjectResponse response = new ProjectResponse(
                1L, "My Project", "Description", Instant.parse("2026-01-01T00:00:00Z"));
        when(projectService.create(any(ProjectRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"My Project","description":"Description"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/v1/projects/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("My Project"));
    }

    @Test
    void create_shouldReturn400WhenNameBlank() throws Exception {
        mockMvc.perform(post("/api/v1/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"","description":"Description"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findById_shouldReturn404WhenNotFound() throws Exception {
        when(projectService.findById(99L))
                .thenThrow(new ResourceNotFoundException("Project with id 99 not found"));

        mockMvc.perform(get("/api/v1/projects/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_shouldReturn409OnDuplicateName() throws Exception {
        when(projectService.create(any(ProjectRequest.class)))
                .thenThrow(new DuplicateResourceException("A project with name 'My Project' already exists"));

        mockMvc.perform(post("/api/v1/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"My Project","description":"Description"}
                                """))
                .andExpect(status().isConflict());
    }

    @Test
    void create_shouldReturn409OnDataIntegrityViolation() throws Exception {
        when(projectService.create(any(ProjectRequest.class)))
                .thenThrow(new org.springframework.dao.DataIntegrityViolationException("constraint violated"));

        mockMvc.perform(post("/api/v1/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"My Project","description":"Description"}
                                """))
                .andExpect(status().isConflict());
    }
}
