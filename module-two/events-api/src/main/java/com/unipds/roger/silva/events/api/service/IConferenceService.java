package com.unipds.roger.silva.events.api.service;

import com.unipds.roger.silva.events.api.dto.ConferenceRequestDTO;
import com.unipds.roger.silva.events.api.dto.ConferenceResponseDTO;

public interface IConferenceService {
    ConferenceResponseDTO addConference(ConferenceRequestDTO dto);
    ConferenceResponseDTO getConferenceById(Long id);
}
