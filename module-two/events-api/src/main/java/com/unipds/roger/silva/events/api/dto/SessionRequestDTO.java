package com.unipds.roger.silva.events.api.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record SessionRequestDTO(String title, LocalDate startDate, LocalDateTime startTime,
                                ConferenceRequestDTO conference) {
}
