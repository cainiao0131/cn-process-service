package org.cainiao.process;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProcessApplication {

    public static void main(String[] args) {
        new SpringApplication(ProcessApplication.class).run(args);
    }
}
