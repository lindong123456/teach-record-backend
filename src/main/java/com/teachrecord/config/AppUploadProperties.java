package com.teachrecord.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.upload")
public record AppUploadProperties(String dir) {}
