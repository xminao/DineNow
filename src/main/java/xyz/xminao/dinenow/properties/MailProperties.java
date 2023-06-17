package xyz.xminao.dinenow.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "email.yeah")
public class MailProperties {
    private String account;

    private String password;

    private String host;

    private String port;
}
