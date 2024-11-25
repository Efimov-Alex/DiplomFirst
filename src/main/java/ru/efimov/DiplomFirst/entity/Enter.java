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
@Table(name = "enter")
public class Enter {
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
    private LocalDateTime date_of_enter;

    public Enter(Student student, LocalDateTime date_of_enter) {
        this.student = student;
        this.date_of_enter = date_of_enter;
    }

    @Override
    public String toString() {
        return "Enter [id=" + id + ", date_of_enter=" + date_of_enter + ", student=" + student.toString() +"]";
    }

}
