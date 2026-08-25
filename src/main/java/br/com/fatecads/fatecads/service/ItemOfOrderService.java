package br.com.fatecads.fatecads.service;

import br.com.fatecads.fatecads.entity.ItemOfOrder;
import br.com.fatecads.fatecads.repository.ItemOfOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemOfOrderService {

    @Autowired
    private ItemOfOrderRepository itemOfOrderRepository;

    //Method to save one order item
    public ItemOfOrder save(ItemOfOrder itemOfOrder) {
        itemOfOrder.subTotalUpdate();
        return itemOfOrderRepository.save(itemOfOrder);
    }

    //Method to search all order items
    public List<ItemOfOrder> findAll() {
        return itemOfOrderRepository.findAll();
    }

    //Method to search order items by ID
    public ItemOfOrder findById(Integer id) {
        return itemOfOrderRepository.findById(id).orElse(null);
    }

    //Method to delete one order item by ID
    public void deleteById(Integer id) {
        itemOfOrderRepository.deleteById(id);
    }
}
