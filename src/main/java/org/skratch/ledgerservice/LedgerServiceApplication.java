package org.skratch.ledgerservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@Slf4j
@SpringBootApplication
public class LedgerServiceApplication {

    public static void main(String[] args) {
        String env = System.getProperty("ENV");
        if (env == null || env.isEmpty()) {
            env = System.getenv("ENV");
        }
        if (env == null || env.isEmpty()) {
            env = "dev";
        }

        System.setProperty("spring.profiles.active", env);

        ConfigurableApplicationContext context = new SpringApplicationBuilder(LedgerServiceApplication.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);

        if (context.isRunning()) {
            log.info("Application started with profile: {}", env);
        }
    }

}
