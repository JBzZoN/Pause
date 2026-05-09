package com.josh.life.tool;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component
public class ToolConfiguration {
	
	@Tool(description = "Create a user account")
	public void createUser(String name, String age, String email) {
		System.out.println("User account created--------------------------------");
		System.out.println(name + " : " + age + " : " + email);
	}
	
}
