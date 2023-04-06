package com.reserveat.web.mapper;

import com.reserveat.domain.Photo;
import com.reserveat.web.model.PhotoOutputDto;

import java.util.List;

public class PhotoMapper {
    private PhotoMapper() {
    }

    public static PhotoOutputDto fromPhoto(Photo photo) {
        return new PhotoOutputDto()
            .id(photo.id())
            .url(photo.url());
    }

    public static List<PhotoOutputDto> fromPhotos(List<Photo> photos) {
        return photos.stream().map(PhotoMapper::fromPhoto).toList();
    }
}
