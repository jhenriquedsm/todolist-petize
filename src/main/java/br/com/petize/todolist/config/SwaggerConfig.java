package br.com.petize.todolist.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Petize To-Do List")
                        .description("API para gerenciamento de tarefas (To-Do List) - Teste Técnico para Estágio Backend (Java)")
                        .version("v1.0.0"));
    }
}