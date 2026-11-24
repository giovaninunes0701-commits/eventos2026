package com.curso.eventos.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI documentacaoApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Eventos 2026 - API REST")
                        .version("1.0.0")
                        .description("API para gerenciamento de eventos, locais, organizadores e controle de vagas."));
    }
}
