package com.frank.crud.service;

import org.springframework.stereotype.Service;

import com.frank.crud.entity.Employee;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class EmployeeService {

    Map<Long,Employee> data = new ConcurrentHashMap<>();

    public Employee getEmployeeById(Long id) {
        return data.get(id);
    }

    public List<Employee> getEmployees() {
        return data.values().stream().toList();
    }

    public void saveEmployee(Employee employee) {
        data.put(employee.getId(),employee);
    }

    public void deleteEmployee(Long id) {
        data.remove(id);
    }
}
