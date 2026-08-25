package br.com.fatecads.fatecads.controller;

import br.com.fatecads.fatecads.entity.ItemOfOrder;
import br.com.fatecads.fatecads.service.ItemOfOrderService;
import br.com.fatecads.fatecads.service.OrderService;
import br.com.fatecads.fatecads.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/item-of-order")
public class ItemOfOrderController {

    @Autowired
    private ItemOfOrderService itemOfOrderService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @PostMapping("/save")
    public String save(@ModelAttribute ItemOfOrder itemOfOrder) {
        itemOfOrderService.save(itemOfOrder);
        return "redirect:/item-of-order/list";
    }

    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("itemsOfOrder", itemOfOrderService.findAll());
        return "itemoforder/itemOfOrderList";
    }

    @GetMapping({"/new", "/create"})
    public String newForm(Model model) {
        model.addAttribute("itemOfOrder", new ItemOfOrder());
        model.addAttribute("orders", orderService.findAll());
        model.addAttribute("products", productService.findAll());
        return "itemoforder/itemOfOrderForm";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        itemOfOrderService.deleteById(id);
        return "redirect:/item-of-order/list";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        model.addAttribute("itemOfOrder", itemOfOrderService.findById(id));
        model.addAttribute("orders", orderService.findAll());
        model.addAttribute("products", productService.findAll());
        return "itemoforder/itemOfOrderForm";
    }
}
