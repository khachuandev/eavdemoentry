package spring.EAVDemoPrice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.EAVDemoPrice.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
