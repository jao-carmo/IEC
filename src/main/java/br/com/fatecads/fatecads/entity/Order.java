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
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer orderId;

    private LocalDate orderDate;

    private Double orderTotal;

    @ManyToOne
    @JoinColumn(name = "user_id_fk")
    private User user;

    @ManyToOne
    @JoinColumn(name = "studentId_fk")
    private Student student;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<ItemOfOrder> item;

    //Method to calculate the total
    public Double totalCalculate(){
        java.math.BigDecimal total = java.math.BigDecimal.ZERO;

        if(item != null){
            for(ItemOfOrder item : item){
                total = total.add(java.math.BigDecimal.valueOf(item.getSubtotal()));
            }
        }
        return total.setScale(2, java.math.RoundingMode.HALF_UP).doubleValue();
    }

    //Method to update the total
    public void totalUpdate(){
        this.orderTotal = totalCalculate();
    }
}
