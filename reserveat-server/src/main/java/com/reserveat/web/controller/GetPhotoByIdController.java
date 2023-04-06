package com.reserveat.web.controller;

import com.reserveat.service.PhotoService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GetPhotoByIdController {
    private final PhotoService photoService;

    public GetPhotoByIdController(PhotoService photoService) {
        this.photoService = photoService;
    }

    @GetMapping(
        value = "/photos/{id}",
        produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE}
    )
    public byte[] getPhoto(@PathVariable("id") String id) {
        return photoService.getPhoto(id);
    }
}
