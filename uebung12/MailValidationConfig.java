package ch.zhaw.freelancer4u.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class MailValidationConfig {

    @Bean
    WebClient disifyClient(WebClient.Builder builder,
            @Value("${freelancer.disify.base-url}") String baseUrl) {
        return builder.baseUrl(baseUrl).build();
    }
}
