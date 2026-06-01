package br.com.fatecads.fatecads.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class User {

    //Attributes
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer userId;

    @Column(nullable = false, length = 40)
    private String fullName;

    @Column(nullable = false, length = 40)
    private String email;

    @Column(nullable = false, length = 40)
    private String login;

    @Column(nullable = false, length = 255)
    private String password;

    private String role = "ROLE_USER";

    @Column(length = 80)
    private String resetToken;

    private LocalDateTime resetTokenExpiry;
}
