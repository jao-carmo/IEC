package br.com.fatecads.fatecads.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer orderId;

    private LocalDate orderDate;

    private Double orderTotal;

    @ManyToOne
    @JoinColumn(name = "studentId_fk")
    private Student student;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<ItemOfOrder> item;

    //Method to calculate the total
    public Double totalCalculate(){
        Double total = 0.0;

        if(item != null){
            for(ItemOfOrder item : item){
                total += item.getSubtotal();
            }
        }
        return total;
    }

    //Method to update the total
    public void totalUpdate(){
        this.orderTotal = totalCalculate();
    }
}