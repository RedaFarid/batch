package com.batch;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BatchingApplication {

	public static void main(String[] args) {
		Application.launch(ApplicationContext.class, args);
	}

}
