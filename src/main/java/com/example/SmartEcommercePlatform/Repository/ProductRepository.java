package com.example.SmartEcommercePlatform.Repository;

import com.example.SmartEcommercePlatform.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product,Long> {

}
