package com.unipds.roger.silva.events.api.model;

import com.unipds.roger.silva.events.api.dto.UserRequestDTO;
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
@Table(name = "tbl_user")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "user_name", length = 255, nullable = false)
    private String name;
    @Column(name = "user_email", length = 100, nullable = false, unique = true)
    private String email;
    @Column(name = "user_password", length = 255, nullable = false)
    private String userPassword;

    public static User create(String name, String email, String userPassword){
        return new User(null, name, email, userPassword);
    }
}
