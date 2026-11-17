package com.unipds.roger.silva.events.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tbl_conference")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Conference {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "conference_id")
    private Long conferenceId;
    @Column(name = "name", length = 255, nullable = false)
    private String name;
    @Column(name = "address", length = 255)
    private String address;

    public static Conference create(String name, String address){
        return  new Conference(null, name, address);
    }
}
