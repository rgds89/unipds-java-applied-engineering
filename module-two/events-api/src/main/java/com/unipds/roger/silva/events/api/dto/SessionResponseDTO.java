package com.unipds.roger.silva.events.api.dto;

import com.unipds.roger.silva.events.api.model.Conference;
import com.unipds.roger.silva.events.api.model.Session;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record SessionResponseDTO(Long sessionId, String title, LocalDate startDate, LocalDateTime startTime,
                                 ConferenceResponseDTO conference) {

    public static SessionResponseDTO fromEntityToDto(Session session) {
        var conferenceDTO = ConferenceResponseDTO.fromEntityToDto(session.getConference());
        return SessionResponseDTO.builder()
                .sessionId(session.getSessionId())
                .startDate(session.getStartDate())
                .startTime(session.getStartTime())
                .conference(conferenceDTO)
                .build();
    }
}
