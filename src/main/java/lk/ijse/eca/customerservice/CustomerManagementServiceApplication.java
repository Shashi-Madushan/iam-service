package lk.ijse.eca.customerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CustomerManagementServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerManagementServiceApplication.class, args);
    }

}
