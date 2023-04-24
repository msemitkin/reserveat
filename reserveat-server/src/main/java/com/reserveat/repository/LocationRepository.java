package com.reserveat.repository;

import com.reserveat.domain.Location;
import com.reserveat.domain.LocationCoordinates;
import com.reserveat.domain.DayWorkingHours;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class LocationRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public LocationRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public Location save(Location location) {
        int locationId = saveLocation(location.restaurantId(), location);
        saveLocationWorkingHours(locationId, location.workingHours());
        return new Location(
            locationId,
            location.restaurantId(),
            location.address(),
            location.latitude(),
            location.longitude(),
            location.phone(),
            location.workingHours()
        );
    }

    public List<Location> getByRestaurantId(int restaurantId) {
        List<Integer> locations = getLocationIdsByRestaurantId(restaurantId);
        return locations.stream()
            .map(this::findById)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toList();
    }

    public List<LocationCoordinates> getLocationsCoordinates() {
        try (
            var stream = jdbcTemplate.queryForStream("""
                    SELECT * FROM restaurant_location
                    """, Map.of(), (rs, rowNum) -> new LocationCoordinates(
                    rs.getInt("id"),
                    rs.getInt("restaurant_id"),
                    rs.getDouble("latitude"),
                    rs.getDouble("longitude")
                )
            )
        ) {
            return stream.toList();
        }
    }

    public Optional<Location> findByReservationId(int id) {
        try {
            Integer locationId = jdbcTemplate.queryForObject("""
                    SELECT l.id FROM restaurant_location l
                    JOIN "table" t ON l.id = t.location_id
                    JOIN reservation r on t.id = r.table_id
                    WHERE r.id = :id
                    """,
                Map.of("id", id),
                Integer.class
            );
            return locationId == null ? Optional.empty() : findById(locationId);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    public List<Location> findAll() {
        Set<Integer> ids;
        try (var stream = jdbcTemplate.queryForStream(
            "SELECT id FROM restaurant_location",
            Map.of(),
            (rs, rosNum) -> rs.getInt("id")
        )
        ) {
            ids = stream.collect(Collectors.toSet());
        }
        return ids.stream().map(this::findById)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toList();
    }

    public Optional<Location> findById(int id) {
        try {
            Map<DayOfWeek, DayWorkingHours> workingHours = getWorkingHours(id);
            Location location = jdbcTemplate.queryForObject(
                "SELECT * FROM restaurant_location WHERE id = :id",
                Map.of("id", id),
                (rs, rowNum) -> new Location(
                    rs.getInt("id"),
                    rs.getInt("restaurant_id"),
                    rs.getString("address"),
                    rs.getDouble("latitude"),
                    rs.getDouble("longitude"),
                    rs.getString("phone"),
                    workingHours
                )
            );
            return Optional.ofNullable(location);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Transactional
    public Location update(Location location) {
        jdbcTemplate.update("""
                UPDATE restaurant_location
                SET address = :address,
                    latitude = :latitude,
                    longitude = :longitude,
                    phone = :phone
                WHERE id = :id
                """, Map.of(
                "id", location.id(),
                "address", location.address(),
                "latitude", location.latitude(),
                "longitude", location.longitude(),
                "phone", location.phone()
            )
        );
        deleteWorkingHours(location.id());
        saveLocationWorkingHours(location.id(), location.workingHours());
        return location;
    }

    @Transactional
    public void deleteById(int id) {
        deleteWorkingHours(id);
        jdbcTemplate.update(
            "DELETE FROM location_photo WHERE location_id = :id",
            Map.of("id", id)
        );

        Set<Integer> tableIds;
        try (
            Stream<Integer> stream = jdbcTemplate.queryForStream("""
                    SELECT "table".id as tbl_id FROM "table" WHERE location_id = :id
                    """,
                Map.of("id", id),
                (rs, rowNum) -> rs.getInt("tbl_id")
            )
        ) {
            tableIds = stream.collect(Collectors.toSet());
        }
        if (!tableIds.isEmpty()) {
            jdbcTemplate.update("""
                DELETE FROM reservation r WHERE r.table_id IN (:table_ids)
                """, Map.of("table_ids", tableIds));
            jdbcTemplate.update("""
                DELETE FROM "table" t WHERE t.id IN (:table_ids)
                """, Map.of("table_ids", tableIds));
        }
    }

    private Map<DayOfWeek, DayWorkingHours> getWorkingHours(int id) {
        Map<DayOfWeek, DayWorkingHours> workingHours = new EnumMap<>(DayOfWeek.class);
        try (var stream = jdbcTemplate.queryForStream(
            "SELECT * FROM location_working_hour WHERE location_id = :id",
            Map.of("id", id),
            (rs, rowNum) -> Map.entry(
                DayOfWeek.valueOf(rs.getString("day_of_week")),
                new DayWorkingHours(
                    LocalTime.parse(rs.getString("open_at")),
                    LocalTime.parse(rs.getString("close_at"))
                )
            )
        )) {
            stream.forEach(entry -> workingHours.put(entry.getKey(), entry.getValue()));
        }
        return workingHours;
    }


    private int saveLocation(int restaurantId, Location location) {
        return Objects.requireNonNull(jdbcTemplate.queryForObject(
            """
                INSERT INTO restaurant_location (restaurant_id, address, latitude, longitude, phone)
                VALUES (:restaurant_id, :address, :latitude, :longitude, :phone)
                RETURNING id
                """, Map.of(
                "restaurant_id", restaurantId,
                "address", location.address(),
                "latitude", location.latitude(),
                "longitude", location.longitude(),
                "phone", location.phone()
            ), Integer.class
        ));
    }

    private void saveLocationWorkingHours(int locationId, Map<DayOfWeek, DayWorkingHours> workingHours) {
        for (var dayOfWeekDayWorkingHoursEntry : workingHours.entrySet()) {
            DayWorkingHours hours = dayOfWeekDayWorkingHoursEntry.getValue();
            DayOfWeek day = dayOfWeekDayWorkingHoursEntry.getKey();
            jdbcTemplate.update("""
                INSERT INTO location_working_hour(location_id, day_of_week, open_at, close_at)
                VALUES(:location_id, :day_of_week, :open_at, :close_at)
                """, Map.of(
                "location_id", locationId,
                "day_of_week", day.name(),
                "open_at", hours.from(),
                "close_at", hours.to()
            ));
        }
    }

    private void deleteWorkingHours(int locationId) {
        jdbcTemplate.update(
            "DELETE FROM location_working_hour WHERE location_id = :id",
            Map.of("id", locationId)
        );
    }

    private List<Integer> getLocationIdsByRestaurantId(int restaurantId) {
        try (var stream = jdbcTemplate.queryForStream(
            "SELECT restaurant_location.id FROM restaurant_location WHERE restaurant_location.restaurant_id = :id",
            Map.of("id", restaurantId),
            (rs, rowNum) -> rs.getInt("id")
        )) {
            return stream.toList();
        }
    }
}
