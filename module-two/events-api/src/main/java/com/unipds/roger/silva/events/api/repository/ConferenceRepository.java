package com.unipds.roger.silva.events.api.repository;

import com.unipds.roger.silva.events.api.model.Conference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConferenceRepository extends JpaRepository<Conference, Long> {
}
