package com.unipds.roger.silva.events.api.controller;

import com.unipds.roger.silva.events.api.dto.SubscriptionRequestDTO;
import com.unipds.roger.silva.events.api.dto.SubscriptionResponseDTO;
import com.unipds.roger.silva.events.api.service.ISubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SubscriptionController {
    @Autowired
    private ISubscriptionService subscriptionService;

    @PostMapping("/subscription")
    public ResponseEntity<SubscriptionResponseDTO> addSubscription(@RequestBody SubscriptionRequestDTO subscriptionRequestDTO) {
        return ResponseEntity.created(null).body(SubscriptionResponseDTO.fromEntityToDto(subscriptionService.addSubscription(subscriptionRequestDTO)));
    }

    @GetMapping("/subscription/user/{id}")
    public ResponseEntity<List<SubscriptionResponseDTO>> getSubscriptionByUser(@PathVariable Long id) {
        var subscriptions = subscriptionService.getByUser(id);
        return ResponseEntity.ok(subscriptions.stream().map(SubscriptionResponseDTO::fromEntityToDto).toList());
    }

    @GetMapping("/subscription/session/{id}")
    public ResponseEntity<List<SubscriptionResponseDTO>> getSubscriptionBySession(@PathVariable Long id) {
        var subscriptions = subscriptionService.getBySession(id);
        return ResponseEntity.ok(subscriptions.stream().map(SubscriptionResponseDTO::fromEntityToDto).toList());
    }
}
