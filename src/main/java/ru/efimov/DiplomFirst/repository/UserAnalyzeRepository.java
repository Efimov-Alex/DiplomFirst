package ru.efimov.DiplomFirst.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.efimov.DiplomFirst.entity.UserAnalyze;

import javax.transaction.Transactional;
import java.util.List;

public interface UserAnalyzeRepository extends JpaRepository<UserAnalyze, Long> {
    List<UserAnalyze> findByStudentId(Long studentId);

    @Transactional
    void deleteByStudentId(long studentId);
}
