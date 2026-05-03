package com.teachrecord.config;

import com.teachrecord.service.FileStorageService;
import java.io.IOException;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class UploadDirectoryInitializer {

    private final FileStorageService fileStorageService;

    public UploadDirectoryInitializer(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @EventListener(ContextRefreshedEvent.class)
    public void init() throws IOException {
        fileStorageService.init();
    }
}
