package assessment.parkinglotservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ParkingLotServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ParkingLotServiceApplication.class, args);
    }

    //TODO: Setup API documentation using Asciidoctor (https://spring.io/projects/spring-restdocs)
    //TODO: Use AOP to set up forensic logging for all requests
    //TODO: Create /error page mapping
}
