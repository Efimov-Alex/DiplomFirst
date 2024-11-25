package ru.efimov.DiplomFirst.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "openmaterial")
public class OpenMaterial {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    @JsonIgnore
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "material_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Material material;

    @Column(nullable = false)
    private LocalDateTime date_of_open;

    public OpenMaterial(Student student, Material material, LocalDateTime date_of_open) {
        this.student = student;
        this.material = material;
        this.date_of_open = date_of_open;
    }


    /*
    public OpenMaterial(Optional<Student> student, Optional<Material> material, LocalDateTime dateOfOpen) {
        this.student = student;
        this.material = material;
        this.date_of_open = date_of_open;
    }

     */
}

