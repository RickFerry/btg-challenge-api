package tech.buildrun.btgpactual.orderms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "tech.buildrun.btgpactual.orderms.repository")
public class ApplicationConfig {
    // Configurações adicionais da aplicação podem ser adicionadas aqui
}
