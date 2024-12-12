package be.pxl;

import be.pxl.repository.EmployeeRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static junit.framework.TestCase.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class EmployeeTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Container
    private static MySQLContainer sqlContainer = new MySQLContainer("mysql:5.7.37");

    @DynamicPropertySource
    static void registerMySQLProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", sqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", sqlContainer::getUsername);
        registry.add("spring.datasource.password", sqlContainer::getPassword);
    }

    @Test
    public void testCreateEmployee() {

        Employee employee = Employee.builder()
                .age(24)
                .name("Bart")
                .position("student")
                .build();

        try {
            String employeeString = objectMapper.writeValueAsString(employee);

            mockMvc.perform(MockMvcRequestBuilders.post("/employee")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(employeeString))
                    .andExpect(status().isCreated());

            assertEquals(1,employeeRepository.findAll().size());

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testFindEmployeeById() throws Exception {
        // Create an employee in the repository
        Employee employee = Employee.builder()
                .age(30)
                .name("John Doe")
                .position("Developer")
                .build();
        Employee savedEmployee = employeeRepository.save(employee);

        // Perform the GET request for the saved employee by ID
        mockMvc.perform(MockMvcRequestBuilders.get("/employee/{id}", savedEmployee.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testFindAllEmployees() throws Exception {
        // Create two employees in the repository
        Employee employee1 = Employee.builder()
                .age(25)
                .name("Alice")
                .position("Engineer")
                .build();

        Employee employee2 = Employee.builder()
                .age(35)
                .name("Bob")
                .position("Manager")
                .build();

        employeeRepository.save(employee1);
        employeeRepository.save(employee2);

        // Perform the GET request to fetch all employees
        mockMvc.perform(MockMvcRequestBuilders.get("/employee")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertEquals(2, employeeRepository.findAll().size());
    }




}
