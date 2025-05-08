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
@Table(name = "task")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    @JsonIgnore
    private Long id;
    @Column(nullable = false)
    private String title;
    private String description;

    @Column(nullable = false)
    private LocalDateTime creation_time;

    @Column(nullable = false)
    private LocalDateTime deadline;

    public Task(String title, String description, LocalDateTime creation_time, LocalDateTime deadline) {
        this.title = title;
        this.description = description;
        this.creation_time = creation_time;
        this.deadline = deadline;
    }
}
