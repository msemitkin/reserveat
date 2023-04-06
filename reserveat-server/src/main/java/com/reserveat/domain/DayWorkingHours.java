package com.reserveat.domain;

import java.time.LocalTime;

public record DayWorkingHours(LocalTime from, LocalTime to) {
}
