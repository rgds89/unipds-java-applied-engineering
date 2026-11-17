package com.unipds.roger.silva.events.api.model;

import com.unipds.roger.silva.events.api.dto.SessionRequestDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_session")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "session_id")
    private Long sessionId;
    @Column(name = "title", length = 255, nullable = false, unique = true)
    private String title;
    @Column(name = "start_date")
    private LocalDate startDate;
    @Column(name = "start_time")
    private LocalDateTime startTime;
    @ManyToOne
    @JoinColumn(name = "conference_id")
    private Conference conference;

    public static Session create(String title, Conference conference){
        return new Session(null, title, LocalDate.now(), LocalDateTime.now(), conference);
    }
}
