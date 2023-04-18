package com.reserveat.repository;

import com.reserveat.domain.Restaurant;
import com.reserveat.domain.exception.RestaurantNotFoundException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class RestaurantRepository {
    private static final RowMapper<Restaurant> RESTAURANT_ROW_MAPPER =
        (rs, rowNum) -> new Restaurant(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("description")
        );

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public RestaurantRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @NonNull
    public Restaurant createRestaurant(@NonNull Restaurant restaurant) {
        String query = "insert into restaurant (\"name\", description) values (:name, :description) returning id";
        var params = Map.of("name", restaurant.getName(), "description", restaurant.getDescription());

        Integer idOfRestaurant = jdbcTemplate.queryForObject(query, params, Integer.class);

        return new Restaurant(idOfRestaurant, restaurant.getName(), restaurant.getDescription());
    }

    public Optional<Restaurant> getById(int id) {
        String query = "select * from restaurant where id = :id";
        Map<String, Integer> params = Map.of("id", id);
        try {
            Restaurant restaurant = jdbcTemplate.queryForObject(query, params, RESTAURANT_ROW_MAPPER);
            return Optional.ofNullable(restaurant);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Restaurant> getAll() {
        try (var stream = jdbcTemplate.queryForStream("select * from restaurant", Map.of(), RESTAURANT_ROW_MAPPER)) {
            return stream.toList();
        }
    }

    @NonNull
    public Restaurant update(@NonNull Restaurant restaurant) {
        String query = """
            update restaurant
            set "name" = :name,
                description = :description
            where id = :id
            """;
        var params = Map.of(
            "id", restaurant.getId(),
            "name", restaurant.getName(),
            "description", restaurant.getDescription()
        );

        int numberOfRowsAffected = jdbcTemplate.update(query, params);

        if (numberOfRowsAffected == 0) {
            throw new RestaurantNotFoundException();
        } else {
            return restaurant;
        }
    }

    public void delete(int id) {
        jdbcTemplate.update("delete from restaurant where id = :id", Map.of("id", id));
    }
}
