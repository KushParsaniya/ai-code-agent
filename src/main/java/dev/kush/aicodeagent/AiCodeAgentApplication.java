package dev.kush.aicodeagent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@ConfigurationPropertiesScan
public class AiCodeAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiCodeAgentApplication.class, args);
    }

}
