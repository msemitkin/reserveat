package com.reserveat.repository;

import com.reserveat.domain.Photo;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.net.URL;
import java.util.List;
import java.util.Map;

@Repository
public class PhotoRepository {
    private static final RowMapper<Photo> PHOTO_ROW_MAPPER = (rs, rowNum) -> new Photo(
        rs.getString("id"),
        rs.getString("photo_url")
    );

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public PhotoRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Photo addLocationPhoto(String uuid, int locationId, URL url) {
        jdbcTemplate.update("""
            INSERT INTO location_photo (id, location_id, photo_url)
            VALUES (:id, :location_id, :photo_url)
            """, Map.of(
            "id", uuid,
            "location_id", locationId,
            "photo_url", url.toString()
        ));
        return new Photo(uuid, url.toString());
    }

    public List<Photo> getLocationPhotos(int locationId) {
        try (var stream = jdbcTemplate.queryForStream(
            "SELECT * FROM location_photo WHERE location_id = :location_id",
            Map.of("location_id", locationId),
            PHOTO_ROW_MAPPER)) {
            return stream.toList();
        }
    }

    public void delete(String id) {
        jdbcTemplate.update("DELETE FROM location_photo WHERE id = :id",
            Map.of("id", id));
    }
}
