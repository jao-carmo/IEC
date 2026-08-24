package br.com.fatecads.fatecads.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer productId;

    @Column(nullable = false, length = 40)
    private String productDescription;

    private Double productPrice;

    @Column(nullable = false, length = 10)
    private String productUnit;

    @Column(nullable = false, length = 30)
    private String productBrand;

    @OneToMany(mappedBy = "product")
    private List<ItemOfOrder> item;
}
