package lk.ijse.eca.iamservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class iamServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(iamServiceApplication.class, args);
    }

}
