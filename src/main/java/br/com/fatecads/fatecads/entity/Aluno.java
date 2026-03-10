package br.com.fatecads.fatecads.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Aluno {

    //Attributes
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer idAluno;

    @Column(nullable = false, length = 40)
    private String nomeAluno;

    @Column(length = 100)
    private String emailAluno;

    @Column(nullable = false, length = 11)
    private String telefoneAluno;

    @Column(nullable = false, length = 100)
    private String enderecoAluno;

    @Column(nullable = false, length = 11)
    private String cpfAluno;

    @Column(nullable = false)
    private String raAluno;
}
