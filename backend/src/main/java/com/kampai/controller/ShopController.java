package com.kampai.controller;

import com.kampai.entity.Shop;
import com.kampai.repository.ShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/shops")
public class ShopController {

    @Autowired
    private ShopRepository shopRepository;

    @GetMapping
    public List<Shop> getAllShops() {
        return shopRepository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Shop> getShopById(@PathVariable Long id) {
        return shopRepository.findById(id);
    }

    @PostMapping
    public Shop createShop(@RequestBody Shop shop) {
        return shopRepository.save(shop);
    }

    @PutMapping("/{id}")
    public Shop updateShop(@PathVariable Long id, @RequestBody Shop updatedShop) {
        return shopRepository.findById(id).map(shop -> {
            shop.setName(updatedShop.getName());
            shop.setAddress(updatedShop.getAddress());
            shop.setPhone(updatedShop.getPhone());
            shop.setSeats(updatedShop.getSeats());
            shop.setGenre(updatedShop.getGenre());
            return shopRepository.save(shop);
        }).orElseGet(() -> {
            updatedShop.setId(id);
            return shopRepository.save(updatedShop);
        });
    }

    @DeleteMapping("/{id}")
    public void deleteShop(@PathVariable Long id) {
        shopRepository.deleteById(id);
    }
}
