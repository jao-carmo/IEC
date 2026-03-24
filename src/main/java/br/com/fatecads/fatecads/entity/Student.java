package br.com.fatecads.fatecads.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Student {

    //Attributes
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer studentId;

    @Column(nullable = false, length = 40)
    private String fullName;

    @Column(length = 100)
    private String email;

    @Column(nullable = false, length = 11)
    private String phone;

    @Column(nullable = false, length = 100)
    private String address;

    @Column(nullable = false, length = 11)
    private String cpf;

    @Column(nullable = false)
    private String registrationNumber;
}
