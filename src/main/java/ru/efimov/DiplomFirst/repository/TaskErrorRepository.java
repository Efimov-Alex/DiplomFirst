package ru.efimov.DiplomFirst.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.efimov.DiplomFirst.entity.TaskError;
import ru.efimov.DiplomFirst.entity.TaskPassed;

import javax.transaction.Transactional;
import java.util.List;

public interface TaskErrorRepository extends JpaRepository<TaskError, Long> {
    List<TaskError> findByStudentId(Long studentId);

    @Transactional
    void deleteByStudentId(long studentId);

    List<TaskError> findByTaskId(Long taskId);

    @Transactional
    void deleteByTaskId(long taskId);


}