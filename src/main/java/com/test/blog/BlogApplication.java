package com.test.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootApplication
public class BlogApplication {
	public static void main(String[] args) {
		// Run the application and get the application context
		ApplicationContext ctx = SpringApplication.run(BlogApplication.class, args);

		// Get the StringRedisTemplate bean from the context
		StringRedisTemplate template = ctx.getBean(StringRedisTemplate.class);

		// Fetch and print the values from Redis
		String val1 = template.opsForValue().get("testkey1");
		System.out.println(val1);

		// String val2 = template.opsForValue().get("testkey2");
		// System.out.println(val2);
	}
}
