package ru.efimov.DiplomFirst.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "taskanalyze")
public class TaskAnalyze {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    @JsonIgnore
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "task_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Task task;

    @Column(nullable = false)
    private String descripation;

    @Column(nullable = false)
    private String characteristic;

    @Column(nullable = false)
    private String value;

    public TaskAnalyze(Task task, String descripation, String characteristic, String value) {
        this.task = task;
        this.descripation = descripation;
        this.characteristic = characteristic;
        this.value = value;
    }
}


