package timp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TimpApplication {
    public static void main(String[] args) {
        SpringApplication.run(TimpApplication.class, args);
    }
}
