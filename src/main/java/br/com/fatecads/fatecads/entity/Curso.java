package br.com.fatecads.fatecads.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Curso {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer IdCurso;

    @Column(nullable = false, length = 100)
    private String NomeCurso;

    @Column(nullable = false, length = 40)
    private String PeriodoCurso;

    @Column(nullable = false, length = 40)
    private Integer CargaHorariaCurso;
}
