package com.unipds.roger.silva.events.api.controller;

import com.unipds.roger.silva.events.api.dto.ConferenceRequestDTO;
import com.unipds.roger.silva.events.api.dto.ConferenceResponseDTO;
import com.unipds.roger.silva.events.api.dto.DeleteSuccessDTO;
import com.unipds.roger.silva.events.api.service.IConferenceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ConferenceController {
    private final IConferenceService conferenceService;

    public ConferenceController(IConferenceService conferenceService) {
        this.conferenceService = conferenceService;
    }

    @PostMapping("/conference")
    public ResponseEntity<ConferenceResponseDTO> addConference(@RequestBody @Valid ConferenceRequestDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(ConferenceResponseDTO.fromEntityToDto(conferenceService.addConference(dto)));
    }

    @PutMapping("/conference/{id}")
    public ResponseEntity<ConferenceResponseDTO> updateConference(@RequestBody @Valid ConferenceRequestDTO dto, @PathVariable Long id){
        return  ResponseEntity.ok(ConferenceResponseDTO.fromEntityToDto(conferenceService.updateConference(dto, id)));
    }

    @DeleteMapping("/conference/{id}")
    public ResponseEntity<DeleteSuccessDTO> deleteConference(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(DeleteSuccessDTO.builder().message("Deleted success").build());
    }

    @GetMapping("/conference/{id}")
    public ResponseEntity<ConferenceResponseDTO> getConferenceById(@PathVariable Long id){
        return ResponseEntity.ok(ConferenceResponseDTO.fromEntityToDto(conferenceService.getConferenceById(id)));
    }

    @GetMapping("/conference")
    public ResponseEntity<List<ConferenceResponseDTO>> getAllConference(){
        return ResponseEntity.ok(conferenceService.getAllConference().stream().map(ConferenceResponseDTO::fromEntityToDto).toList());
    }
}
