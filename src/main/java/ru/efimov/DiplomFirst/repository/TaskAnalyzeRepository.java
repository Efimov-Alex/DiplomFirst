package ru.efimov.DiplomFirst.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.efimov.DiplomFirst.entity.MaterialAnalyze;
import ru.efimov.DiplomFirst.entity.TaskAnalyze;
import ru.efimov.DiplomFirst.entity.TaskError;

import javax.transaction.Transactional;
import java.util.List;

public interface TaskAnalyzeRepository extends JpaRepository<TaskAnalyze, Long> {

    List<TaskAnalyze> findByTaskId(Long taskId);

    @Transactional
    void deleteByTaskId(long taskId);

}
