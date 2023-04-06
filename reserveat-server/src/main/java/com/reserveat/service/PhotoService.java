package com.reserveat.service;

import com.reserveat.domain.Photo;
import com.reserveat.domain.exception.PhotoNotFoundException;
import com.reserveat.repository.PhotoRepository;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class PhotoService {
    private static final String UPLOAD_DIRECTORY = System.getProperty("user.home");

    private final PhotoRepository photoRepository;
    private final URL photosBasePath = new URL("http", "localhost", 8080, "/photos");

    public PhotoService(PhotoRepository photoRepository) throws MalformedURLException {
        this.photoRepository = photoRepository;
    }

    public Photo addLocationPhoto(
        int locationId,
        byte[] content,
        String fileName
    ) {
        try {
            UUID imageId = UUID.randomUUID();
            Path directory = Paths.get(UPLOAD_DIRECTORY, "reserveat", imageId.toString());
            Files.createDirectories(directory);
            Path fileNameAndPath = Paths.get(directory.toString(), fileName);
            Files.write(fileNameAndPath, content);

            URL imageUrl = new URL(photosBasePath.toString().concat("/").concat(imageId.toString()));
            return photoRepository.addLocationPhoto(imageId.toString(), locationId, imageUrl);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public List<Photo> getLocationPhotos(int locationId) {
        return photoRepository.getLocationPhotos(locationId);
    }

    public byte[] getPhoto(String id) {
        Path directory = Paths.get(UPLOAD_DIRECTORY, "reserveat", id);
        if (!Files.exists(directory)) {
            throw new PhotoNotFoundException();
        }
        try (Stream<Path> stream = Files.walk(directory, 1)) {
            Path first = stream.skip(1).findFirst().orElseThrow();
            try (var is = Files.newInputStream(first)) {
                return is.readAllBytes();
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void deletePhoto(String id) {
        try {
            Path directory = Paths.get(UPLOAD_DIRECTORY, "reserveat", id);
            if (Files.exists(directory)) {
                FileUtils.forceDelete(directory.toFile());
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        photoRepository.delete(id);
    }
}
