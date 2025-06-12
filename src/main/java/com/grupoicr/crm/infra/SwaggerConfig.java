package com.grupoicr.crm.infra;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SwaggerConfig {
    @Value("${app.url}")
    private String serverUrl;

    @Value("${app.env}")
    private String environment;

    private static Info infos;

    private static void setInfos() {
        infos = new Info()
                .title("Test de Office")
                .version("1.0")
                .description("""
                    Este projeto foi desenvolvido para testar o implementação do only office no sistema fort para manipular doc no frontend
                    
                    Desenvolvido por:
                      - Gabriel Bessi
                    """);
    }

    @Bean
    public OpenAPI customOpenAPI() {
        setInfos();

        List<Server> servers = new ArrayList<>();
        servers.add(new Server()
                .url(serverUrl)
                .description(environment + " Server"));

        return new OpenAPI()
                .info(infos)
                .servers(servers)
                .components(new Components());
    }

}
