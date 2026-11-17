package com.unipds.roger.silva.events.api.service;

import com.unipds.roger.silva.events.api.dto.ConferenceRequestDTO;
import com.unipds.roger.silva.events.api.dto.ConferenceResponseDTO;
import com.unipds.roger.silva.events.api.model.Conference;
import com.unipds.roger.silva.events.api.repository.ConferenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConferenceServiceImpl implements IConferenceService{
    @Autowired
    ConferenceRepository conferenceRepository;

    @Override
    public ConferenceResponseDTO addConference(ConferenceRequestDTO dto) {
        return buildConferenceDto(conferenceRepository.save(Conference.create(dto.name(), dto.address())));
    }

    @Override
    public ConferenceResponseDTO getConferenceById(Long id) {
        var conference = conferenceRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Conference not found"));
        return buildConferenceDto(conference);
    }

    private ConferenceResponseDTO buildConferenceDto(Conference conference){
        return ConferenceResponseDTO.builder()
                .conferenceId(conference.getConferenceId())
                .name(conference.getName())
                .address(conference.getAddress())
                .build();
    }
}
