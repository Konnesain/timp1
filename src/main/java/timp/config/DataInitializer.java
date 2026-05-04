package timp.config;

import timp.model.User;
import timp.repository.BuildingRepository;
import timp.repository.EmployeeBuildingAccessRepository;
import timp.repository.EmployeeRepository;
import timp.repository.SensorRepository;
import timp.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(UserRepository userRepository,
                               EmployeeRepository employeeRepository,
                               BuildingRepository buildingRepository,
                               EmployeeBuildingAccessRepository accessRepository,
                               SensorRepository sensorRepository,
                               PasswordEncoder passwordEncoder) {
        return args -> {
            // Default users
            if (userRepository.findByUsername("admin").isEmpty()) {
                userRepository.save(new User("admin", passwordEncoder.encode("admin123")));
                System.out.println(">>> Created default admin user (admin/admin123)");
            }
            if (userRepository.findByUsername("user").isEmpty()) {
                userRepository.save(new User("user", passwordEncoder.encode("user123")));
                System.out.println(">>> Created default user (user/user123)");
            }
        };
    }
}
