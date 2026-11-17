package com.unipds.roger.silva.events.api.repository;

import com.unipds.roger.silva.events.api.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
}
