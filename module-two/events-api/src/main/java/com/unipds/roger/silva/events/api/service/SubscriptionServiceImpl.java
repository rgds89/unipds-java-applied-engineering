package com.unipds.roger.silva.events.api.service;

import com.unipds.roger.silva.events.api.dto.SubscriptionRequestDTO;
import com.unipds.roger.silva.events.api.dto.SubscriptionResponseDTO;
import com.unipds.roger.silva.events.api.model.Subscription;
import com.unipds.roger.silva.events.api.model.SubscriptionId;
import com.unipds.roger.silva.events.api.model.Session;
import com.unipds.roger.silva.events.api.model.User;
import com.unipds.roger.silva.events.api.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscriptionServiceImpl implements ISubscriptionService{
    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private UserServiceImpl userService;

    @Override
    public SubscriptionResponseDTO addSubscription(SubscriptionRequestDTO dto) {
        var subscription = buildSubscription(dto);
        return null;
    }

    @Override
    public List<SubscriptionResponseDTO> getAllByUser(Long userId) {
        return List.of();
    }

    @Override
    public List<SubscriptionResponseDTO> getAllBySession(Long sessionId) {
        return List.of();
    }

    private Subscription buildSubscription(SubscriptionRequestDTO dto){
        var user = userService.getUserById(dto.userId());
        return null;
    }

}
