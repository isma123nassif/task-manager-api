package com.isma.taskmanager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class ProjectIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void projectCrudFlow() throws Exception {
        MvcResult createResult = mockMvc.perform(post("/api/v1/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Portfolio Project","description":"A sample project"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Portfolio Project"))
                .andReturn();

        JsonNode created = objectMapper.readTree(createResult.getResponse().getContentAsString());
        long id = created.get("id").asLong();

        mockMvc.perform(get("/api/v1/projects/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Portfolio Project"));

        mockMvc.perform(put("/api/v1/projects/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Updated Project","description":"Updated description"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Project"));

        mockMvc.perform(get("/api/v1/projects")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Updated Project"))
                .andExpect(jsonPath("$.totalElements").value(1));

        mockMvc.perform(delete("/api/v1/projects/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/projects/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_shouldReturn409ForCaseInsensitiveDuplicateName() throws Exception {
        MvcResult createResult = mockMvc.perform(post("/api/v1/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Web Studio","description":"A sample project"}
                                """))
                .andExpect(status().isCreated())
                .andReturn();

        long id = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asLong();

        try {
            mockMvc.perform(post("/api/v1/projects")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"name":"web studio","description":"A duplicate project"}
                                    """))
                    .andExpect(status().isConflict());
        } finally {
            mockMvc.perform(delete("/api/v1/projects/{id}", id))
                    .andExpect(status().isNoContent());
        }
    }
}
