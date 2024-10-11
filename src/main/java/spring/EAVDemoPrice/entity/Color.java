package spring.EAVDemoPrice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "colors")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Color {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
}
