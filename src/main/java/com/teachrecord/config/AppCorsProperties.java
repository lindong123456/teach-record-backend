package com.teachrecord.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.cors")
public class AppCorsProperties {

    /**
     * Spring Ant-style origin patterns for /api/** and /uploads/**.
     * When empty, all origins are allowed (see GlobalCorsConfig). For public internet deployments,
     * set explicit patterns (e.g. https://your-domain.com).
     */
    private List<String> allowedOriginPatterns = new ArrayList<>();

    public List<String> getAllowedOriginPatterns() {
        return allowedOriginPatterns;
    }

    public void setAllowedOriginPatterns(List<String> allowedOriginPatterns) {
        this.allowedOriginPatterns = allowedOriginPatterns;
    }
}
