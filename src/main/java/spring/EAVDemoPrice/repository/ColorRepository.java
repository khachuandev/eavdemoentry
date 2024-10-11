package spring.EAVDemoPrice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.EAVDemoPrice.entity.Color;

import java.util.Optional;

public interface ColorRepository extends JpaRepository<Color, Long> {
    Optional<Color> findByName(String name);
}
