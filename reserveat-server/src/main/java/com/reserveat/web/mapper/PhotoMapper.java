package com.reserveat.web.mapper;

import com.reserveat.domain.Photo;
import com.reserveat.web.model.PhotoOutputDto;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class PhotoMapper {
    private PhotoMapper() {
    }

    public static PhotoOutputDto fromPhoto(Photo photo) {
        try {
            return new PhotoOutputDto()
                .id(photo.getId())
                .url(new URI(photo.getUrl()));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<PhotoOutputDto> fromPhotos(List<Photo> photos) {
        return photos.stream().map(PhotoMapper::fromPhoto).toList();
    }
}
