package com.unipds.roger.silva.events.api.repository;

import com.unipds.roger.silva.events.api.model.Subscription;
import com.unipds.roger.silva.events.api.model.SubscriptionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, SubscriptionId> {
}
