package com.batch;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class BatchingApplication {

	public static void main(String[] args) {
		Application.launch(ApplicationContext.class, args);
	}

}
