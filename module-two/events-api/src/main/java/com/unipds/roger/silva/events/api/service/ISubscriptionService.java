package com.unipds.roger.silva.events.api.service;

import com.unipds.roger.silva.events.api.dto.SubscriptionRequestDTO;
import com.unipds.roger.silva.events.api.model.Subscription;

import java.util.List;

public interface ISubscriptionService {
    Subscription addSubscription(SubscriptionRequestDTO dto);

    List<Subscription> getByUser(Long userId);

    List<Subscription> getBySession(Long sessionId);
}
