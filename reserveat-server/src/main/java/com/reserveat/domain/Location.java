package com.reserveat.domain;

import com.reserveat.web.DayWorkingHours;

import java.time.DayOfWeek;
import java.util.Map;

public record Location(
    Integer id,
    String address,
    Double latitude,
    Double longitude,
    String phone,
    Map<DayOfWeek, DayWorkingHours> workingHours
) {}
