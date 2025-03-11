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
@Table(name = "useranalyze")
public class UserAnalyze {
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

    @Column(nullable = false)
    private Integer atribute_id;

    @Column(nullable = false)
    private String characteristic;

    @Column(nullable = false)
    private String value;

    public UserAnalyze(Student student, Integer atribute_id, String characteristic, String value) {
        this.student = student;
        this.atribute_id = atribute_id;
        this.characteristic = characteristic;
        this.value = value;
    }
}

