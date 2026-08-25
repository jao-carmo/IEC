package br.com.fatecads.fatecads.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ItemOfOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer itemId;

    private Integer amount;

    private Double price;

    private Double subtotal;

    @ManyToOne
    @JoinColumn(name = "orderId_fk")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "productId_fk")
    private Product product;

    //Method to calculate the subtotal
    public Double subTotalCalculated() {
        return amount * price;
    }

    //Method to update the subtotal
    public void subTotalUpdate(){
        this.subtotal = subTotalCalculated();
    }
}
