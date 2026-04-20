package timp.config;

import timp.model.Building;
import timp.model.Employee;
import timp.model.EmployeeBuildingAccess;
import timp.model.Sensor;
import timp.model.Sensor.SensorType;
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

            // Default buildings (only if empty)
            if (buildingRepository.count() == 0) {
                Building b1 = new Building("Главный корпус");
                b1.setPositionX(50);
                b1.setPositionY(80);
                b1.setWidth(200);
                b1.setHeight(150);
                b1.setDescription("Основной административный корпус");
                buildingRepository.save(b1);

                Building b2 = new Building("Лаборатория");
                b2.setPositionX(350);
                b2.setPositionY(100);
                b2.setWidth(160);
                b2.setHeight(120);
                b2.setDescription("Исследовательская лаборатория");
                buildingRepository.save(b2);

                System.out.println(">>> Created 2 default buildings with map data");
            }

            // Default employees (only if table is empty)
            if (employeeRepository.count() == 0) {
                Employee emp1 = employeeRepository.save(new Employee("Иванов И.И.", "Инженер"));
                Employee emp2 = employeeRepository.save(new Employee("Петров П.П.", "Охранник"));
                Employee emp3 = employeeRepository.save(new Employee("Сидоров С.С.", "Бухгалтер"));
                Employee emp4 = employeeRepository.save(new Employee("Козлов К.К.", "Директор"));

                // Load buildings (could be just created or already existing)
                Building b1 = buildingRepository.findById(1L).orElseThrow();
                Building b2 = buildingRepository.findById(2L).orElseThrow();

                // Иванов И.И. — access to building 1 only, currently inside building 1
                emp1.setBuildingId(b1.getId());
                employeeRepository.save(emp1);
                accessRepository.save(new EmployeeBuildingAccess(emp1.getId(), b1.getId()));

                // Петров П.П. — access to both, currently inside building 2
                emp2.setBuildingId(b2.getId());
                employeeRepository.save(emp2);
                accessRepository.save(new EmployeeBuildingAccess(emp2.getId(), b1.getId()));
                accessRepository.save(new EmployeeBuildingAccess(emp2.getId(), b2.getId()));

                // Сидоров С.С. — no access, outside
                // Козлов К.К. — access to both, currently inside building 1
                emp4.setBuildingId(b1.getId());
                employeeRepository.save(emp4);
                accessRepository.save(new EmployeeBuildingAccess(emp4.getId(), b1.getId()));
                accessRepository.save(new EmployeeBuildingAccess(emp4.getId(), b2.getId()));

                System.out.println(">>> Created 4 default employees with building access");
            }

            // Default sensors (one temperature + one camera per building, only if empty)
            if (sensorRepository.count() == 0) {
                Building b1 = buildingRepository.findById(1L).orElseThrow();
                Building b2 = buildingRepository.findById(2L).orElseThrow();

                // Temperature sensors
                Sensor temp1 = new Sensor(b1, "Датчик температуры #1");
                sensorRepository.save(temp1);

                Sensor temp2 = new Sensor(b2, "Датчик температуры #2");
                sensorRepository.save(temp2);

                // Cameras
                Sensor cam1 = new Sensor(b1, "Камера #1");
                cam1.setType(SensorType.CAMERA);
                sensorRepository.save(cam1);

                Sensor cam2 = new Sensor(b2, "Камера #2");
                cam2.setType(SensorType.CAMERA);
                sensorRepository.save(cam2);

                System.out.println(">>> Created 4 default sensors (2 temperature + 2 cameras)");
            }
        };
    }
}
