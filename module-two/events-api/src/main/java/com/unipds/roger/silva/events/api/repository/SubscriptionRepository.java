package com.unipds.roger.silva.events.api.repository;

import com.unipds.roger.silva.events.api.model.Session;
import com.unipds.roger.silva.events.api.model.Subscription;
import com.unipds.roger.silva.events.api.model.SubscriptionId;
import com.unipds.roger.silva.events.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, SubscriptionId> {
    List<Subscription> findByIdUser(User user);

    List<Subscription> findByIdSession(Session session);
}
