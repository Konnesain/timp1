package timp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@PropertySource("classpath:mail.properties")
public class TimpApplication {
    public static void main(String[] args) {
        SpringApplication.run(TimpApplication.class, args);
    }
}
