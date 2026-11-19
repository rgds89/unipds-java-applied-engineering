package com.unipds.roger.silva.events.api.dto;

import com.unipds.roger.silva.events.api.model.Subscription;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record SubscriptionResponseDTO(UserResponseDTO user, SessionResponseDTO session, String uniqueId,
                                      LocalDateTime createdAt, int level) {
    public static SubscriptionResponseDTO fromEntityToDto(Subscription subscription) {
        var userResponseDto = UserResponseDTO.fromEntityToDto(subscription.getId().getUser());
        var sessionResponseDto = SessionResponseDTO.fromEntityToDto(subscription.getId().getSession());
        return SubscriptionResponseDTO.builder()
                .user(userResponseDto)
                .session(sessionResponseDto)
                .uniqueId(subscription.getUniqueId())
                .createdAt(subscription.getCreatedAt())
                .level(subscription.getLevel())
                .build();
    }
}
