package ru.efimov.DiplomFirst.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.efimov.DiplomFirst.entity.Enter;
import ru.efimov.DiplomFirst.entity.Exit;

import javax.transaction.Transactional;
import java.util.List;

public interface ExitRepository extends JpaRepository<Exit, Long> {
    List<Exit> findByStudentId(Long studentId);

    @Transactional
    void deleteByStudentId(long studentId);


}
