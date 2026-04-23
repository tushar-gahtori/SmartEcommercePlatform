package com.example.SmartEcommercePlatform.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Many items belong to one order
    @ManyToOne
    @JoinColumn(name="order_id")
    private Order order;

    //Many items refer to one product
    @ManyToOne
    @JoinColumn(name="product_id")
    private Product product;

    private int quantity;
}
