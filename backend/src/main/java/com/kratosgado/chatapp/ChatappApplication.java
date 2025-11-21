package com.kratosgado.chatapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChatappApplication {

  public static void main(String[] args) {
    SpringApplication.run(ChatappApplication.class, args);
  }

  // @Bean
  // public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
  // return args -> {
  // System.out.println("CommandLineRunner");
  // String[] beans = ctx.getBeanDefinitionNames();
  // Arrays.stream(beans).forEach(System.out::println);
  // };
  // }
}
