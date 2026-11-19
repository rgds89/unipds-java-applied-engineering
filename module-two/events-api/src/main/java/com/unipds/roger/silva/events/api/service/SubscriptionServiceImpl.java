package com.unipds.roger.silva.events.api.service;

import com.unipds.roger.silva.events.api.dto.SubscriptionRequestDTO;
import com.unipds.roger.silva.events.api.model.Subscription;
import com.unipds.roger.silva.events.api.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscriptionServiceImpl implements ISubscriptionService {
    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private IUserService userService;

    @Autowired
    private ISessionService sessionService;

    @Override
    public Subscription addSubscription(SubscriptionRequestDTO subscriptionRequestDTO) {
        var user = userService.getUserById(subscriptionRequestDTO.userId());
        var session = sessionService.getSessionById(subscriptionRequestDTO.sessionId());
        return subscriptionRepository.save(Subscription.create(user, session));
    }

    @Override
    public List<Subscription> getByUser(Long userId) {
        return subscriptionRepository.findByIdUser(userService.getUserById(userId));
    }

    @Override
    public List<Subscription> getBySession(Long sessionId) {
        return subscriptionRepository.findByIdSession(sessionService.getSessionById(sessionId));
    }

}
