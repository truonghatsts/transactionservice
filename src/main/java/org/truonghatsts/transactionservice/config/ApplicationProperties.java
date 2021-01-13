package org.truonghatsts.transactionservice.config;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Fenix truonghatsts@gmail.com
 */

@Data
@Configuration
@ConfigurationProperties("app")
public class ApplicationProperties {

    private Security security;

    @Data
    public static class Security {
        private String apiKey;
        private String allowed;
    }
}
