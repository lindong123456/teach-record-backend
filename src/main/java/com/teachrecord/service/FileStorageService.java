package com.teachrecord.service;

import com.teachrecord.config.AppUploadProperties;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class FileStorageService {

    private final Path uploadRoot;

    public FileStorageService(AppUploadProperties props) {
        String dir = props.dir() != null ? props.dir() : "uploads";
        this.uploadRoot = Path.of(dir).toAbsolutePath().normalize();
    }

    public void init() throws IOException {
        Files.createDirectories(uploadRoot);
    }

    public String store(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "empty file");
        }
        String original = file.getOriginalFilename() != null ? file.getOriginalFilename() : "file";
        String ext = extension(original);
        String stored = UUID.randomUUID() + (ext.isEmpty() ? "" : "." + ext);
        Path target = uploadRoot.resolve(stored).normalize();
        if (!target.startsWith(uploadRoot)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid path");
        }
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "save failed");
        }
        return stored;
    }

    public void deleteIfExists(String storedFilename) {
        try {
            Path target = uploadRoot.resolve(storedFilename).normalize();
            if (target.startsWith(uploadRoot)) {
                Files.deleteIfExists(target);
            }
        } catch (IOException ignored) {
        }
    }

    private static String extension(String name) {
        int i = name.lastIndexOf('.');
        if (i < 0 || i == name.length() - 1) {
            return "";
        }
        return name.substring(i + 1).toLowerCase(Locale.ROOT);
    }
}
