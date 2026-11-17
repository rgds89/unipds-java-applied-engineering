package com.unipds.roger.silva.events.api.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record SessionResponseDTO(Long sessionId, String title, LocalDate startDate, LocalDateTime startTime,
                                 Long conferenceId) {
}
