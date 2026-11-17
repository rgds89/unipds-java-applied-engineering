package com.unipds.roger.silva.events.api.dto;

import lombok.Builder;

@Builder
public record ConferenceResponseDTO(Long conferenceId, String name, String address) {
}
