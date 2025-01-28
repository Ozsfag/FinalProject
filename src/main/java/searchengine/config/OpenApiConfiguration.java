package searchengine.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {
    @Bean
    public OpenAPI openAPI() {
        Server localHost = new Server();
        localHost.setUrl("http://localhost:8080/");
        localHost.setDescription("Local env");

        Contact developersContact = new Contact();
        developersContact.setName("Artem Sergienko");
        developersContact.setEmail("Ozsfag3154artem@gail.com");

        License license = new License()
                .name("GNU GPLv3")
                .url("https://choosealicense.com/licenses/gpl-3.0/");

        Info info = new Info()
                .title("Search Engine API")
                .version("1.0")
                .description("API documentation for the Search Engine project")
                .contact(developersContact)
                .license(license);

        return new OpenAPI().info(info).addServersItem(localHost);

    }
}
