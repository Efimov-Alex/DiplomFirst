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
@Table(name = "profileanalyze")
public class ProfileAnalyze {
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

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime created_date;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime last_updates;

    public ProfileAnalyze(Student student, LocalDateTime created_date, LocalDateTime last_updates) {
        this.student = student;
        this.created_date = created_date;
        this.last_updates = last_updates;
    }

    @Override
    public String toString() {
        return "ProfileAnalyze [id=" + student + ", createdDate=" + created_date +
                 ", lastUpdates=" + last_updates +"]";
    }


}
