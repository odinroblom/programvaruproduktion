package backend;

import frontend.CashierSystem;
import javafx.application.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

//Program startup class
@SpringBootApplication(scanBasePackages={ "backend", "frontend" })
public class SpringBootApp implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger( SpringBootApp.class );

    public static void main(String[] args) {
        log.info( "Starting up boot server and launching cashier system");
        Application.launch( CashierSystem.class, args );
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Override
    public void run(String... args) {
    }
}