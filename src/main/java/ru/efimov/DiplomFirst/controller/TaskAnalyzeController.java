package ru.efimov.DiplomFirst.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.efimov.DiplomFirst.entity.*;
import ru.efimov.DiplomFirst.repository.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@CrossOrigin(origins = "http://localhost:8083")
@RestController
@RequestMapping("/api")
public class TaskAnalyzeController {
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskErrorRepository taskErrorRepository;

    @Autowired
    private TaskPassedRepository taskPassedRepository;

    @Autowired
    private TaskAnalyzeRepository taskAnalyzeRepository;

    private static final Logger logger = LogManager.getLogger(TaskAnalyzeController.class);


    @GetMapping("/tasks/{taskId}/taskErrors")
    public ResponseEntity<Float> getAverageErrors(@PathVariable(value = "taskId") Long taskId
                                                        ) {
        List<TaskError> taskErrors = taskErrorRepository.findByTaskId(taskId);
        int lengthList = taskErrors.size();
        float totalSum = 0;
        for (TaskError t1 : taskErrors){
            totalSum += t1.getCount_errors();
        }

        float averageCount = (float) totalSum / lengthList;

        logger.info("Получение среднего количество ошибок");

        return new ResponseEntity<>(averageCount, HttpStatus.CREATED);
    }

    @GetMapping("/tasks/{taskId}/metrics")
    public ResponseEntity<TaskAnalyze> getTaskMetrics(@PathVariable(value = "taskId") Long taskId
    ) {
        List<TaskAnalyze> taskAnalyzes = taskAnalyzeRepository.findByTaskId(taskId);

        if (taskAnalyzes.size() != 1){
            logger.error("Not found taskAnalyze with taskId = " + taskId);
            throw new ResourceNotFoundException("Not found taskAnalyze with taskId = " + taskId);
        }
        TaskAnalyze oldTaskAnalyze = taskAnalyzes.get(0);

        TaskAnalyze newTaskAnalyze = new TaskAnalyze();

        newTaskAnalyze.setId(oldTaskAnalyze.getId());
        newTaskAnalyze.setTask(oldTaskAnalyze.getTask());


        Optional<Task> currentTask = taskRepository.findById(taskId);
        Task _task = currentTask.get();

        List<TaskError> taskErrors = taskErrorRepository.findByTaskId(taskId);
        int lengthList = taskErrors.size();
        float totalSum = 0;
        for (TaskError t1 : taskErrors){
            totalSum += t1.getCount_errors();
        }
        if (lengthList == 0){
            logger.error("Not found TaskError");
            throw new ResourceNotFoundException("Not found TaskError");
        }

        float averageCount = (float) totalSum / lengthList;

        newTaskAnalyze.setCount_error(averageCount);

        List<TaskPassed> taskPasseds = taskPassedRepository.findByTaskId(taskId);

        double totalTimeSum = 0;
        long timeCount = 0;

        for(TaskPassed t1 : taskPasseds){
            LocalDateTime l1 = t1.getDate_of_passed();
            if (l1.compareTo(_task.getDeadline()) > 0){
                continue;
            }
            if (l1.compareTo(_task.getCreation_time()) < 0){
                continue;
            }

            long minutes = ChronoUnit.MINUTES.between(_task.getCreation_time(), l1);

            long minutesUnderDeadline = ChronoUnit.MINUTES.between(_task.getCreation_time(), _task.getDeadline());
            totalTimeSum += (double) minutes / minutesUnderDeadline;
            timeCount += 1;

            System.out.println(totalTimeSum);

        }
        if (timeCount == 0){
            logger.error("Not found TaskPassed");
            throw new ResourceNotFoundException("Not found TaskPassed");
        }



        float averageTime = (float) totalTimeSum / timeCount;
        newTaskAnalyze.setMean_time(averageTime);


        taskAnalyzeRepository.save(newTaskAnalyze);



        logger.info("Получение метрик заданий");
        return new ResponseEntity<>(newTaskAnalyze, HttpStatus.CREATED);
    }




    @GetMapping("/taskAnalyze/{id}")
    public ResponseEntity<TaskAnalyze> gettaskAnalyzeByStudentId(@PathVariable(value = "id") Long id) {
        TaskAnalyze taskAnalyze = taskAnalyzeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found taskAnalyze with id = " + id));

        logger.info("Получение TaskAnalyze по id " + id);

        return new ResponseEntity<>(taskAnalyze, HttpStatus.OK);
    }

    @PostMapping("/tasks/{taskId}/taskAnalyzes")
    public ResponseEntity<TaskAnalyze> createtaskAnalyze(@PathVariable(value = "taskId") Long taskId,
                                                                 @RequestBody TaskAnalyze taskAnalyzeRequest) {
        TaskAnalyze taskAnalyze = taskRepository.findById(taskId).map(task -> {
            taskAnalyzeRequest.setTask(task);
            return taskAnalyzeRepository.save(taskAnalyzeRequest);
        }).orElseThrow(() -> new ResourceNotFoundException("Not found Task with id = " + taskId));

        logger.info("Создание TaskAnalyze");
        return new ResponseEntity<>(taskAnalyze, HttpStatus.CREATED);
    }

    @PutMapping("/taskAnalyze/{id}")
    public ResponseEntity<TaskAnalyze> updateTaskAnalyze(@PathVariable("id") long id, @RequestBody TaskAnalyze taskAnalyzeRequest) {
        TaskAnalyze taskAnalyze = taskAnalyzeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TaskAnalyzeId " + id + "not found"));

        taskAnalyze.setMean_time(taskAnalyzeRequest.getMean_time());

        taskAnalyze.setCount_error(taskAnalyzeRequest.getCount_error());

        logger.info("Обновление TaskAnalyze по id " + id);

        return new ResponseEntity<>(taskAnalyzeRepository.save(taskAnalyze), HttpStatus.OK);
    }

    @DeleteMapping("/taskAnalyze/{id}")
    public ResponseEntity<HttpStatus> deleteTaskAnalyze(@PathVariable("id") long id) {
        taskAnalyzeRepository.deleteById(id);

        logger.info("Удаление TaskAnalyze по id " + id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/tasks/{taskId}/taskAnalyzes")
    public ResponseEntity<List<TaskAnalyze>> deleteAlltaskAnalyzesOftask(@PathVariable(value = "taskId") Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            logger.error("Not found Task with id = " + taskId);
            throw new ResourceNotFoundException("Not found Task with id = " + taskId);
        }

        taskAnalyzeRepository.deleteByTaskId(taskId);
        logger.info("Удаление всех TaskAnalyze по taskId " + taskId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}