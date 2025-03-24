package ru.efimov.DiplomFirst.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "student")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false)
    @JsonIgnore
    private Long id;
    @Column(nullable = false, unique=true)
    private String username;
    @Column(nullable = false)
    private String password;
    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime date_of_registration;




    public Student(String username, String password, LocalDateTime date_of_registration) {

        this.username = username;
        this.password = password;
        this.date_of_registration = date_of_registration;


    }



    @Override
    public String toString() {
        return "Student [id=" + id + ", username=" + username + ", password=" + password
                + ", date_of_registration=" + date_of_registration +"]";
    }


}
