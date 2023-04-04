package com.reserveat.web;

import java.time.LocalTime;

public record DayWorkingHours(LocalTime from, LocalTime to) {
}
