package com.unipds.roger.silva.events.api.dto;

import java.time.LocalDateTime;

public record SubscriptionRequestDTO(Long userId, Long sessionId, String uniqueId,
                                     LocalDateTime createdAt, int level) {
}
