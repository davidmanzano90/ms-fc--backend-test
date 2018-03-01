package com.scmspain;

import com.scmspain.configuration.InfrastructureConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({InfrastructureConfiguration.class})
public class MsFcTechTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsFcTechTestApplication.class, args);
    }
}
