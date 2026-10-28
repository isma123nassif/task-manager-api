package com.isma.taskmanager;

import org.springframework.boot.SpringApplication;

public class TestTaskManagerApiApplication {

	public static void main(String[] args) {
		SpringApplication.from(TaskManagerApiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
