package com.reserveat.web.mapper;

import com.reserveat.domain.DayWorkingHours;
import com.reserveat.domain.Location;
import com.reserveat.web.model.HoursInputDto;
import com.reserveat.web.model.LocationInputDto;
import com.reserveat.web.model.LocationOutputDto;
import com.reserveat.web.model.LocationWithRestaurantOutputDto;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class LocationMapper {
    private LocationMapper() {
    }

    public static LocationOutputDto fromLocation(Location location) {
        List<HoursInputDto> hoursInputDtos = toDto(location.workingHours());

        return new LocationOutputDto()
            .id(location.id())
            .restaurantId(location.restaurantId())
            .address(location.address())
            .latitude(location.latitude())
            .longitude(location.longitude())
            .phone(location.phone())
            .hours(hoursInputDtos);
    }

    public static Location toLocation(LocationInputDto locationInputDto, int restaurantId) {
        return toLocation(null, locationInputDto, restaurantId);
    }

    public static Location toLocation(Integer id, LocationInputDto locationInputDto, int restaturantId) {
        List<HoursInputDto> hours = locationInputDto.getHours();
        Map<DayOfWeek, DayWorkingHours> workingHours = hours.stream()
            .collect(toMap(
                it -> DayOfWeek.valueOf(it.getDayOfWeek().name()),
                it -> new DayWorkingHours(
                    LocalTime.parse(it.getOpens()),
                    LocalTime.parse(it.getCloses())
                )
            ));

        return new Location(
            id,
            restaturantId,
            locationInputDto.getAddress(),
            locationInputDto.getLatitude(),
            locationInputDto.getLongitude(),
            locationInputDto.getPhone(),
            workingHours
        );
    }

    public static List<LocationWithRestaurantOutputDto> toDtos(List<Location> locations) {
        return locations.stream()
            .map(LocationMapper::toDto)
            .toList();
    }

    public static LocationWithRestaurantOutputDto toDto(Location location) {
        List<HoursInputDto> hoursInputDtos = toDto(location.workingHours());
        return new LocationWithRestaurantOutputDto()
            .id(location.id())
            .restaurantId(location.restaurantId())
            .address(location.address())
            .latitude(location.latitude())
            .longitude(location.longitude())
            .workingHours(hoursInputDtos);
    }

    public static List<HoursInputDto> toDto(Map<DayOfWeek, DayWorkingHours> hours) {
        return hours.entrySet().stream()
            .map(it -> new HoursInputDto()
                .dayOfWeek(HoursInputDto.DayOfWeekEnum.fromValue(it.getKey().name()))
                .opens(it.getValue().from().toString())
                .closes(it.getValue().to().toString())
            )
            .toList();
    }
}
