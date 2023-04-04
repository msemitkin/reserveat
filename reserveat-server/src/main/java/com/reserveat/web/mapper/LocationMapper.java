package com.reserveat.web.mapper;

import com.reserveat.domain.Location;
import com.reserveat.web.DayWorkingHours;
import com.reserveat.web.model.HoursInputDto;
import com.reserveat.web.model.LocationInputDto;
import com.reserveat.web.model.LocationOutputDto;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class LocationMapper {
    private LocationMapper() {
    }

    public static LocationOutputDto fromLocation(Location location) {
        Map<DayOfWeek, DayWorkingHours> workingHours = location.workingHours();
        List<HoursInputDto> hoursInputDtos = workingHours.entrySet().stream().map(it -> new HoursInputDto()
                .dayOfWeek(HoursInputDto.DayOfWeekEnum.fromValue(it.getKey().name()))
                .opens(it.getValue().from().toString())
                .closes(it.getValue().to().toString())
            )
            .toList();

        return new LocationOutputDto()
            .id(location.id())
            .address(location.address())
            .latitude(location.latitude())
            .longitude(location.longitude())
            .phone(location.phone())
            .hours(hoursInputDtos);
    }

    public static Location toLocation(LocationInputDto locationInputDto) {
        return toLocation(null, locationInputDto);
    }

    public static Location toLocation(Integer id, LocationInputDto locationInputDto) {
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
            locationInputDto.getAddress(),
            locationInputDto.getLatitude(),
            locationInputDto.getLongitude(),
            locationInputDto.getPhone(),
            workingHours
        );
    }
}
