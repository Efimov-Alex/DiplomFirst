package ru.efimov.DiplomFirst.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.efimov.DiplomFirst.entity.OpenMaterial;
import ru.efimov.DiplomFirst.entity.TaskPassed;

import javax.transaction.Transactional;
import java.util.List;

public interface TaskPassedRepository extends JpaRepository<TaskPassed, Long> {
    List<TaskPassed> findByStudentId(Long studentId);

    @Transactional
    void deleteByStudentId(long studentId);

    List<TaskPassed> findByTaskId(Long taskId);

    @Transactional
    void deleteByTaskId(long taskId);


}