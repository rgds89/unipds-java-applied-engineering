package com.unipds.roger.silva.events.api.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record SubscriptionResponseDTO(UserResponseDTO user, SessionResponseDTO session, String uniqueId, LocalDateTime createdAt, int level) {
}
