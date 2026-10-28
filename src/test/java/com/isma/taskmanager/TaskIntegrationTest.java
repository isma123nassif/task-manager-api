package com.isma.taskmanager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class TaskIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void taskFilteringPaginationAndStatusFlow() throws Exception {
        String projectName = "Tasks Alpha " + UUID.randomUUID();
        long projectId = createProject(projectName);
        long otherProjectId = createProject("Tasks Beta " + UUID.randomUUID());

        try {
            long taskA = createTask(projectId, "Draft spec", "TODO", "HIGH", "2026-05-01");
            long taskB = createTask(projectId, "Review spec", "IN_PROGRESS", "HIGH", "2026-05-02");
            long taskC = createTask(projectId, "Ship spec", "DONE", "LOW", "2026-05-03");
            createTask(otherProjectId, "Unrelated task", "TODO", "LOW", null);

            mockMvc.perform(get("/api/v1/tasks").param("status", "TODO"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalElements").value(2));

            mockMvc.perform(get("/api/v1/tasks").param("priority", "HIGH"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalElements").value(2));

            mockMvc.perform(get("/api/v1/tasks").param("projectId", String.valueOf(projectId)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalElements").value(3));

            mockMvc.perform(get("/api/v1/tasks")
                            .param("projectId", String.valueOf(projectId))
                            .param("status", "TODO")
                            .param("priority", "HIGH"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalElements").value(1))
                    .andExpect(jsonPath("$.content[0].title").value("Draft spec"));

            mockMvc.perform(get("/api/v1/tasks")
                            .param("projectId", String.valueOf(projectId))
                            .param("page", "0")
                            .param("size", "2"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalElements").value(3))
                    .andExpect(jsonPath("$.totalPages").value(2))
                    .andExpect(jsonPath("$.content[0].title").value("Ship spec"));

            mockMvc.perform(get("/api/v1/tasks/{id}", taskA))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.projectName").value(projectName));

            mockMvc.perform(patch("/api/v1/tasks/{id}/status", taskA)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"status\":\"DONE\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("DONE"));
        } finally {
            mockMvc.perform(delete("/api/v1/projects/{id}", projectId)).andExpect(status().isNoContent());
            mockMvc.perform(delete("/api/v1/projects/{id}", otherProjectId)).andExpect(status().isNoContent());
        }
    }

    @Test
    void deleteProjectCascadesToTasks() throws Exception {
        long projectId = createProject("Cascade Project " + UUID.randomUUID());
        long taskId = createTask(projectId, "Cascade task", "TODO", "MEDIUM", null);

        mockMvc.perform(delete("/api/v1/projects/{id}", projectId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/tasks/{id}", taskId))
                .andExpect(status().isNotFound());
    }

    private long createProject(String name) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"%s","description":"sample"}
                                """.formatted(name)))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }

    private long createTask(long projectId, String title, String status, String priority, String dueDate) throws Exception {
        String content = """
                {"title":"%s","status":"%s","priority":"%s","projectId":%d,"dueDate":%s}
                """.formatted(title, status, priority, projectId, dueDate == null ? "null" : "\"" + dueDate + "\"");
        MvcResult result = mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }
}
