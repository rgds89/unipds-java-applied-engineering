package com.unipds.roger.silva.events.api.service;

import com.unipds.roger.silva.events.api.dto.ConferenceRequestDTO;
import com.unipds.roger.silva.events.api.model.Conference;

import java.util.List;

public interface IConferenceService {
    Conference addConference(ConferenceRequestDTO dto);

    Conference updateConference(ConferenceRequestDTO dto, Long id);

    void deleteConference(Long id);

    Conference getConferenceById(Long id);

    List<Conference> getAllConference();
}
