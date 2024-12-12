package be.pxl.services;

import be.pxl.Employee;
import be.pxl.client.NotificationClient;
import be.pxl.model.NotificationRequest;
import be.pxl.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    private final NotificationClient notificationClient;
    public EmployeeService(EmployeeRepository employeeRepository, NotificationClient notificationClient) {
        this.employeeRepository = employeeRepository;
        this.notificationClient = notificationClient;
    }

    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    public Optional<Employee> findById(Long id) {
        return employeeRepository.findById(id);
    }


    public Employee add(Employee employee) {

        NotificationRequest notificationRequest = NotificationRequest.builder()
                .message("Employee Created")
                .sender("Manav").build();

        notificationClient.sendMessage(notificationRequest);

        return employeeRepository.save(employee);


    }
}
