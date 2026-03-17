package com.tutorial.employeemanager.service;

import com.tutorial.employeemanager.entity.Employee;
import com.tutorial.employeemanager.repository.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {
    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee sampleEmployee;

    @BeforeEach
    void setUp() {
        sampleEmployee = Employee
                .builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john@doe.com")
                .department("Engineering")
                .build();
    }

    @Test
    @DisplayName("FindAll returns list of employees")
    void findAll() {
        when(employeeRepository.findAll())
                .thenReturn(List.of(sampleEmployee));

        List<Employee> result = employeeService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail())
                .isEqualTo("john@doe.com");
    }

    @Test
    @DisplayName("FindBy Id returns employee when exists")
    void findById_exists_throwsException() {
        when(employeeRepository.findById(1L))
        .thenReturn(Optional.of(sampleEmployee));

        Employee result = employeeService.findById(1L);

        assertThat(result.getFirstName())
                .isEqualTo("John");
    }

    @Test
    @DisplayName("FindBy Id throws when not exists")
    void findById_not_exists_throwsException() {
        when(employeeRepository.findById(1L))
        .thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.findById(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Employee with id 1 not found");
    }

    @Test
    @DisplayName("Create saves and returns employee")
    void create() {
        when(employeeRepository.save(sampleEmployee))
                .thenReturn(sampleEmployee);

        Employee result = employeeService.create(sampleEmployee);

        assertThat(result.getId()).isEqualTo(1L);
        verify(employeeRepository).save(sampleEmployee);
    }

    @Test
    @DisplayName("Delete removes existing employee")
    void delete() {
        when(employeeRepository.findById(1L))
        .thenReturn(Optional.of(sampleEmployee));
        doNothing().when(employeeRepository).delete(sampleEmployee);

        employeeService.delete(1L);

        verify(employeeRepository).delete(sampleEmployee);
    }
}
