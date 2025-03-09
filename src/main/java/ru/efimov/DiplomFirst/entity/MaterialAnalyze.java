package ru.efimov.DiplomFirst.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "materialanalyze")
public class MaterialAnalyze {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    @JsonIgnore
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "material_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Material material;

    @Column(nullable = false)
    private String descripation;

    @Column(nullable = false)
    private String characteristic;

    @Column(nullable = false)
    private String value;

    public MaterialAnalyze(Material material, String descripation, String characteristic, String value) {
        this.material = material;
        this.descripation = descripation;
        this.characteristic = characteristic;
        this.value = value;
    }
}

