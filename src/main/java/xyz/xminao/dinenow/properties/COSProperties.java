package xyz.xminao.dinenow.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "qcloud.cos")
public class COSProperties {
    private String endpoint;
    private String secretId;
    private String secretKey;
    private String bucketName;
}