package br.com.fatecads.fatecads.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer courseId;

    @Column(nullable = false, length = 100)
    private String courseName;

    @Column(nullable = false, length = 40)
    private String period;

    @Column(nullable = false, length = 40)
    private Integer workloadHours;
}
