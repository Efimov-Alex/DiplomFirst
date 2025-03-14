package ru.efimov.DiplomFirst.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.efimov.DiplomFirst.entity.Student;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findFirstByUsername(String username);


    @Query("SELECT s.id FROM Student AS s WHERE s.username = :username")
    Long idByUsername(String username);

}
