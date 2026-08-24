package br.com.fatecads.fatecads.repository;

import br.com.fatecads.fatecads.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {

}
