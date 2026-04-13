package br.com.fatecads.fatecads.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer subjectId;

    @Column(nullable = false, length = 100)
    private String subjectName;

    @Column(nullable = false, length = 15)
    private String code;

    @Column(nullable = false, length = 40)
    private Integer workloadHours;
}
