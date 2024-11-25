package ru.efimov.DiplomFirst.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.efimov.DiplomFirst.entity.Enter;
import ru.efimov.DiplomFirst.entity.Material;

import javax.transaction.Transactional;
import java.util.List;

public interface EnterRepository extends JpaRepository<Enter, Long> {

    List<Enter> findByStudentId(Long studentId);

    @Transactional
    void deleteByStudentId(long studentId);
}

