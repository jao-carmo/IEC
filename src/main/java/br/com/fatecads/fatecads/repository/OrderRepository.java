package br.com.fatecads.fatecads.repository;

import br.com.fatecads.fatecads.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Integer> {

}