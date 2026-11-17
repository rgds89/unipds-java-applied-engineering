package com.unipds.roger.silva.events.api.service;

import com.unipds.roger.silva.events.api.dto.SubscriptionRequestDTO;
import com.unipds.roger.silva.events.api.dto.SubscriptionResponseDTO;

import java.util.List;

public interface ISubscriptionService {
    SubscriptionResponseDTO addSubscription(SubscriptionRequestDTO dto);
    List<SubscriptionResponseDTO> getAllByUser(Long userId);
    List<SubscriptionResponseDTO> getAllBySession(Long sessionId);
}
