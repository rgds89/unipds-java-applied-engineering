package com.unipds.roger.silva.events.api.dto;

import com.unipds.roger.silva.events.api.model.Conference;
import lombok.Builder;

@Builder
public record ConferenceResponseDTO(Long conferenceId, String name, String address) {

    public static ConferenceResponseDTO fromEntityToDto(Conference conference) {
        return ConferenceResponseDTO.builder()
                .conferenceId(conference.getConferenceId())
                .name(conference.getName())
                .address(conference.getAddress())
                .build();
    }
}
