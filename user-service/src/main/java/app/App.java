package app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "controller",
        "service",
        "notificationservice.service.impl",
        "repository",
        "dto",
        "model"
})
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}