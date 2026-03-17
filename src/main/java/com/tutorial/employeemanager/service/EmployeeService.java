package com.tutorial.employeemanager.service;

import com.tutorial.employeemanager.entity.Employee;
import com.tutorial.employeemanager.repository.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;

    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    public Employee findById(Long id) {
        return employeeRepository
                .findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("Employee with id " + id + " not found")
                );
    }

    public Employee create(Employee employee) {
        return employeeRepository.save(employee);
    }

    public Employee update(Long id, Employee employee) {
        Employee updateEmployee = findById(id);
        updateEmployee.setFirstName(employee.getFirstName());
        updateEmployee.setLastName(employee.getLastName());
        updateEmployee.setEmail(employee.getEmail());
        updateEmployee.setDepartment(employee.getDepartment());
        return employeeRepository.save(updateEmployee);

    }

    public void delete(Long id) {
        Employee deleteEmployee = findById(id);
        employeeRepository.delete(deleteEmployee);
    }
}
