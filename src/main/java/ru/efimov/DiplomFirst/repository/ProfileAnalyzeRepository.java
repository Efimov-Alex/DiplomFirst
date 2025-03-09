package ru.efimov.DiplomFirst.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.efimov.DiplomFirst.entity.Enter;
import ru.efimov.DiplomFirst.entity.ProfileAnalyze;
import ru.efimov.DiplomFirst.entity.Student;

import javax.transaction.Transactional;
import java.util.List;

public interface ProfileAnalyzeRepository extends JpaRepository<ProfileAnalyze, Long> {
    List<ProfileAnalyze> findByStudentId(Long studentId);

    @Transactional
    void deleteByStudentId(long studentId);
}
