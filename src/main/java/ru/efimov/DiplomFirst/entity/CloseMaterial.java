package ru.efimov.DiplomFirst.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "closematerial")
public class CloseMaterial {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    @JsonIgnore
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id", nullable = false, insertable = false, updatable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "id", nullable = false, insertable = false, updatable = false)
    private Material material;

    @Column(nullable = false)
    private LocalDateTime Date_of_close;

    public CloseMaterial(Student student, Material material, LocalDateTime Date_of_close) {
        this.student = student;
        this.material = material;
        this.Date_of_close = Date_of_close;
    }


}