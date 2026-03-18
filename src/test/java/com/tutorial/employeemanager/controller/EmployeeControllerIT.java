package com.tutorial.employeemanager.controller;

import com.tutorial.employeemanager.entity.Employee;
import com.tutorial.employeemanager.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EmployeeControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private EmployeeRepository employeeRepository;

    @BeforeEach
    void cleanUp() {
        employeeRepository.deleteAll();
    }

    @Test
    @Order(1)
    @DisplayName("POST /api/employees creates employee")
    void createEmployee() throws Exception {
        Employee employee = Employee.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@doe.com")
                .department("Finance")
                .build();

        mockMvc.perform(
                        post("/api/employees")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(employee))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.department").value("Finance"));
    }

    @Test
    @Order(2)
    @DisplayName("GET /api/employees returns all")
    void getAllEmployee() throws Exception {
        employeeRepository.save(
                Employee.builder()
                        .firstName("A")
                        .lastName("B")
                        .email("a@b.com")
                        .department("IT")
                        .build()
        );

        mockMvc.perform(
                        get("/api/employees")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @Order(3)
    @DisplayName("GET /api/employees/{id} returns 404")
    void getByIdNotFound() throws Exception {
        mockMvc.perform(
                        get("/api/employees/999")
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(4)
    @DisplayName("PUT /api/employees/{id} updates employee")
    void updateEmployee() throws Exception {
        Employee saved = employeeRepository.save(
                Employee.builder()
                        .firstName("Old")
                        .lastName("Name")
                        .email("old@example.com")
                        .department("HR")
                        .build()
        );

        Employee updated = Employee.builder()
                .firstName("New")
                .lastName("Name")
                .email("new@example.com")
                .department("Engineering")
                .build();

        mockMvc.perform(
                        put("/api/employees/" + saved.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updated))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("New"));

    }


    @Test
    @Order(5)
    @DisplayName("DELETE /api/employees/{id} removes")
    void deleteEmployee() throws Exception {
        Employee saved = employeeRepository.save(
                Employee.builder()
                        .firstName("Del")
                        .lastName("Me")
                        .email("del@me.com")
                        .department("Ops")
                        .build()
        );

        mockMvc.perform(delete("/api/employees/" + saved.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/employees/" + saved.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(6)
    @DisplayName("POST with invalid data returns 400")
    void createInvalidReturns400() throws Exception {
        Employee invalid = Employee.builder()
                .firstName("")
                .lastName("")
                .email("bad")
                .build();

        mockMvc.perform(
                        post("/api/employees")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalid))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray());
    }
}
