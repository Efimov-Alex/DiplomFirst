package ru.efimov.DiplomFirst.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.efimov.DiplomFirst.entity.MaterialAnalyze;
import ru.efimov.DiplomFirst.entity.TaskAnalyze;
import ru.efimov.DiplomFirst.entity.TaskError;
import ru.efimov.DiplomFirst.entity.TaskPassed;
import ru.efimov.DiplomFirst.repository.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

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

        return new ResponseEntity<>(averageCount, HttpStatus.CREATED);
    }

    @GetMapping("/tasks/{taskId}/metrics")
    public ResponseEntity<TaskAnalyze> getTaskMetrics(@PathVariable(value = "taskId") Long taskId
    ) {
        List<TaskAnalyze> taskAnalyzes = taskAnalyzeRepository.findByTaskId(taskId);

        if (taskAnalyzes.size() != 1){
            throw new ResourceNotFoundException("Not found taskAnalyze with taskId = " + taskId);
        }
        TaskAnalyze oldTaskAnalyze = taskAnalyzes.get(0);

        TaskAnalyze newTaskAnalyze = new TaskAnalyze();
        newTaskAnalyze.setDeadline(oldTaskAnalyze.getDeadline());
        newTaskAnalyze.setId(oldTaskAnalyze.getId());
        newTaskAnalyze.setTask(oldTaskAnalyze.getTask());
        newTaskAnalyze.setCreation_time(oldTaskAnalyze.getCreation_time());


        List<TaskError> taskErrors = taskErrorRepository.findByTaskId(taskId);
        int lengthList = taskErrors.size();
        float totalSum = 0;
        for (TaskError t1 : taskErrors){
            totalSum += t1.getCount_errors();
        }

        float averageCount = (float) totalSum / lengthList;

        newTaskAnalyze.setCount_error(averageCount);

        List<TaskPassed> taskPasseds = taskPassedRepository.findByTaskId(taskId);

        long totalTimeSum = 0;
        long timeCount = 0;

        for(TaskPassed t1 : taskPasseds){
            LocalDateTime l1 = t1.getDate_of_passed();
            if (l1.compareTo(newTaskAnalyze.getDeadline()) > 0){
                continue;
            }
            if (l1.compareTo(newTaskAnalyze.getCreation_time()) < 0){
                continue;
            }

            long minutes = ChronoUnit.MINUTES.between(newTaskAnalyze.getCreation_time(), l1);
            System.out.println(minutes);
            System.out.println(newTaskAnalyze.getCreation_time());
            System.out.println(l1);
            totalTimeSum += minutes;
            timeCount += 1;

        }

        float averageTime = (float) totalTimeSum / timeCount;
        newTaskAnalyze.setMean_time(averageTime);


        taskAnalyzeRepository.save(newTaskAnalyze);



        return new ResponseEntity<>(newTaskAnalyze, HttpStatus.CREATED);
    }




    @GetMapping("/taskAnalyze/{id}")
    public ResponseEntity<TaskAnalyze> gettaskAnalyzeByStudentId(@PathVariable(value = "id") Long id) {
        TaskAnalyze taskAnalyze = taskAnalyzeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found taskAnalyze with id = " + id));

        return new ResponseEntity<>(taskAnalyze, HttpStatus.OK);
    }

    @PostMapping("/tasks/{taskId}/taskAnalyzes")
    public ResponseEntity<TaskAnalyze> createtaskAnalyze(@PathVariable(value = "taskId") Long taskId,
                                                                 @RequestBody TaskAnalyze taskAnalyzeRequest) {
        TaskAnalyze taskAnalyze = taskRepository.findById(taskId).map(task -> {
            taskAnalyzeRequest.setTask(task);
            return taskAnalyzeRepository.save(taskAnalyzeRequest);
        }).orElseThrow(() -> new ResourceNotFoundException("Not found Task with id = " + taskId));

        return new ResponseEntity<>(taskAnalyze, HttpStatus.CREATED);
    }

    @PutMapping("/taskAnalyze/{id}")
    public ResponseEntity<TaskAnalyze> updateTaskAnalyze(@PathVariable("id") long id, @RequestBody TaskAnalyze taskAnalyzeRequest) {
        TaskAnalyze taskAnalyze = taskAnalyzeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TaskAnalyzeId " + id + "not found"));

        taskAnalyze.setMean_time(taskAnalyzeRequest.getMean_time());
        taskAnalyze.setCreation_time(taskAnalyzeRequest.getCreation_time());
        taskAnalyze.setDeadline(taskAnalyzeRequest.getDeadline());
        taskAnalyze.setCount_error(taskAnalyzeRequest.getCount_error());

        return new ResponseEntity<>(taskAnalyzeRepository.save(taskAnalyze), HttpStatus.OK);
    }

    @DeleteMapping("/taskAnalyze/{id}")
    public ResponseEntity<HttpStatus> deleteTaskAnalyze(@PathVariable("id") long id) {
        taskAnalyzeRepository.deleteById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/tasks/{taskId}/taskAnalyzes")
    public ResponseEntity<List<TaskAnalyze>> deleteAlltaskAnalyzesOftask(@PathVariable(value = "taskId") Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new ResourceNotFoundException("Not found Task with id = " + taskId);
        }

        taskAnalyzeRepository.deleteByTaskId(taskId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}