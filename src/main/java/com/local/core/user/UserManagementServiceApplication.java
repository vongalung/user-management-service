package com.local.core.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.ZonedDateTime;

@SpringBootApplication
@EnableAsync
@RestController
public class UserManagementServiceApplication {
	@GetMapping({"/probe", "/"})
	ZonedDateTime ping() {
		return ZonedDateTime.now();
	}

	public static void main(String[] args) {
		SpringApplication.run(UserManagementServiceApplication.class, args);
	}

}
