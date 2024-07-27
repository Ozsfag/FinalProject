package searchengine.config;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "connection-settings")
public class ConnectionSettings {
    public String userAgent;
    public String referrer;
}
