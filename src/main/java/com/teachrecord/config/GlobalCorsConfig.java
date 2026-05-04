package com.teachrecord.config;

import java.util.Arrays;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class GlobalCorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource(AppCorsProperties corsProperties) {
        CorsConfiguration cfg = new CorsConfiguration();
        List<String> patterns = corsProperties.getAllowedOriginPatterns();
        if (patterns == null || patterns.isEmpty()) {
            /* allowCredentials=false：可用 *，浏览器任意 Origin 可调 API（仍受 JWT 保护） */
            cfg.setAllowedOriginPatterns(List.of("*"));
        } else {
            cfg.setAllowedOriginPatterns(patterns);
        }
        cfg.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setAllowCredentials(false);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", cfg);
        source.registerCorsConfiguration("/uploads/**", cfg);
        return source;
    }
}

