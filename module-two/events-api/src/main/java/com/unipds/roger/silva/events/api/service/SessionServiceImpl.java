package com.unipds.roger.silva.events.api.service;

import com.unipds.roger.silva.events.api.dto.SessionRequestDTO;
import com.unipds.roger.silva.events.api.exception.NotFoundException;
import com.unipds.roger.silva.events.api.model.Session;
import com.unipds.roger.silva.events.api.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SessionServiceImpl implements ISessionService {
    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private IConferenceService conferenceService;

    @Override
    public Session addSession(SessionRequestDTO dto) {
        var conference = conferenceService.getConferenceById(dto.conferenceId());
        return sessionRepository.save(Session.create(dto.title(), conference));
    }

    @Override
    public Session getSessionById(Long id) {
        return sessionRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Session {0} not found", id)));
    }
}
