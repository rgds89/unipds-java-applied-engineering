package com.unipds.roger.silva.events.api.service;

import com.unipds.roger.silva.events.api.dto.SessionRequestDTO;
import com.unipds.roger.silva.events.api.model.Session;

public interface ISessionService {
    Session addSession(SessionRequestDTO dto);

    Session getSessionById(Long id);
}
