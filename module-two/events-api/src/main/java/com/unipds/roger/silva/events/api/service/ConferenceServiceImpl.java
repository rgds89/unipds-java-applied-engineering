package com.unipds.roger.silva.events.api.service;

import com.unipds.roger.silva.events.api.dto.ConferenceRequestDTO;
import com.unipds.roger.silva.events.api.exception.NotFoundException;
import com.unipds.roger.silva.events.api.model.Conference;
import com.unipds.roger.silva.events.api.repository.ConferenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConferenceServiceImpl implements IConferenceService {
    @Autowired
    ConferenceRepository conferenceRepository;

    @Override
    public Conference addConference(ConferenceRequestDTO dto) {
        return conferenceRepository.save(Conference.create(dto.name(), dto.address()));
    }

    @Override
    public Conference updateConference(ConferenceRequestDTO dto, Long id) {
        return conferenceRepository.save(Conference.update(dto, getConferenceById(id)));
    }

    @Override
    public void deleteConference(Long id) {
        conferenceRepository.deleteById(id);
    }

    @Override
    public Conference getConferenceById(Long id) {
        return conferenceRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Conference {0} not found", id)));
    }

    @Override
    public List<Conference> getAllConference() {
        return conferenceRepository.findAll();
    }
}
