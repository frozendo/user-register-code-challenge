package com.swisscom.userregister;

import com.swisscom.userregister.config.properties.OpaServerProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
		OpaServerProperties.class
})
public class UserRegisterApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserRegisterApplication.class, args);
	}

}
