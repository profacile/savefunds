package be.profacile.savefunds.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI saveFundsAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8080");
        devServer.setDescription("Development server");

        Contact contact = new Contact();
        contact.setEmail("contact@profacile.be");
        contact.setName("Profacile SRL");
        contact.setUrl("https://profacile.be");

        License license = new License()
            .name("Proprietary")
            .url("https://profacile.be/license");

        Info info = new Info()
            .title("SaveFunds API")
            .version("1.0.0")
            .contact(contact)
            .description("API de vigilance financière pour PME/SRL belges. " +
                "Analyse automatisée de trésorerie et système de décision tricolore.")
            .license(license);

        return new OpenAPI()
            .info(info)
            .servers(List.of(devServer));
    }
}