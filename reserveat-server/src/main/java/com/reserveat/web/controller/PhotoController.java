package com.reserveat.web.controller;

import com.reserveat.domain.Photo;
import com.reserveat.service.PhotoService;
import com.reserveat.web.api.PhotoApi;
import com.reserveat.web.mapper.PhotoMapper;
import com.reserveat.web.model.PhotoOutputDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

@RestController
public class PhotoController implements PhotoApi {
    private final PhotoService photoService;

    public PhotoController(PhotoService photoService) {
        this.photoService = photoService;
    }

    @Override
    public ResponseEntity<PhotoOutputDto> addPhotoToLocation(
        Integer locationId,
        MultipartFile file
    ) {
        try {
            Photo photo = photoService.addLocationPhoto(locationId, file.getBytes(), file.getOriginalFilename());
            return ResponseEntity.ok(new PhotoOutputDto().id(photo.id()).url(photo.url()));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public ResponseEntity<List<PhotoOutputDto>> getLocationPhotos(Integer locationId) {
        List<Photo> photos = photoService.getLocationPhotos(locationId);
        return ResponseEntity.ok(PhotoMapper.fromPhotos(photos));
    }

    @Override
    public ResponseEntity<Void> deletePhoto(String photoId) {
        photoService.deletePhoto(photoId);
        return ResponseEntity.ok().build();
    }
}
