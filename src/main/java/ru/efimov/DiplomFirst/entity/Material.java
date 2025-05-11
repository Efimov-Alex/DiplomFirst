package ru.efimov.DiplomFirst.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "material")
public class Material {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    @JsonIgnore
    private Long id;
    @Column(nullable = false)
    private String title;
    private String description;

    @Column(nullable = false)
    private Integer time_for_learning;

    public Material(String title, String description, Integer time_for_learning) {
        this.title = title;
        this.description = description;
        this.time_for_learning = time_for_learning;
    }
}
