package cn.zzzzbw.tiny.filemanager;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ManagerService {

    private final Path rootLocation;

    private final String securityKey;

    @Autowired
    public ManagerService(ManagerProperties properties) {
        this.rootLocation = Paths.get(properties.getLocation());
        this.securityKey = properties.getSecurityKey();
    }

    public void store(MultipartFile file) {
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            if (file.isEmpty()) {
                throw new ManagerException("Failed to store empty file " + filename);
            }
            if (filename.contains("..")) {
                // This is a security check
                throw new ManagerException(
                        "Cannot store file with relative path outside current directory "
                                + filename);
            }
            try (InputStream inputStream = file.getInputStream()) {
                if (Files.exists(this.rootLocation.resolve(filename))) {
                    throw new ManagerException("Failed to store file! file: " + filename + " is already exist");
                }
                Files.copy(inputStream, this.rootLocation.resolve(filename),
                        StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new ManagerException("Failed to store file " + filename, e);
        }
    }

    public List<FileInfo> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(this::pathToFileInfo)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new ManagerException("Failed to read stored files", e);
        }
    }


    private FileInfo pathToFileInfo(Path path) {
        try {
            return FileInfo.builder()
                    .path(this.rootLocation.relativize(path))
                    .size(Files.size(path))
                    .lastModifiedTime(new Date(Files.getLastModifiedTime(path).toMillis()))
                    .build();
        } catch (IOException e) {
            throw new ManagerException(e.getMessage(), e);
        }
    }


    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }


    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new ManagerException(
                        "Could not read file: " + filename);

            }
        } catch (MalformedURLException e) {
            throw new ManagerException("Could not read file: " + filename, e);
        }
    }


    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }


    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new ManagerException("Could not initialize storage", e);
        }
    }

    public boolean login(String key) {
        return this.securityKey.equals(key);
    }
}
