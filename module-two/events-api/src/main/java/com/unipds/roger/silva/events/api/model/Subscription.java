package com.unipds.roger.silva.events.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;
import java.util.random.RandomGenerator;

@Entity
@Table(name = "tbl_subscription")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Subscription {
    @Id
    private SubscriptionId id;
    @Column(name = "unique_id", length = 45, nullable = false, unique = true)
    private String uniqueId;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "level")
    private int level;

    public static Subscription create(User user, Session session){
        return new Subscription(
                SubscriptionId.create(user, session),
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                RandomGenerator.getDefault().nextInt()
        );
    }

}
