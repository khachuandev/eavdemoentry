package spring.EAVDemoPrice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.EAVDemoPrice.entity.Size;

import java.util.Optional;

public interface SizeRepository extends JpaRepository<Size, Long> {
    Optional<Size> findByName(String name);
}
