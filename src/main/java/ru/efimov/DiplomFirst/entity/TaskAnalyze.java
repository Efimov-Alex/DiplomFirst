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
    private Float mean_time;


    @Column(nullable = false)
    private Float count_error;

    public TaskAnalyze(Task task, Float mean_time, Float count_error) {
        this.task = task;
        this.mean_time = mean_time;
        this.count_error = count_error;
    }
}


