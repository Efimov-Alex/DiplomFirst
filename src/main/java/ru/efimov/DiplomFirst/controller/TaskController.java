package ru.efimov.DiplomFirst.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.efimov.DiplomFirst.entity.Material;
import ru.efimov.DiplomFirst.entity.Task;
import ru.efimov.DiplomFirst.repository.MaterialRepository;
import ru.efimov.DiplomFirst.repository.TaskRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@CrossOrigin(origins = "http://localhost:8083")
@RestController
@RequestMapping("/api")
public class TaskController {
    @Autowired
    TaskRepository taskRepository;

    private static final Logger logger = LogManager.getLogger(TaskController.class);

    @GetMapping("/tasks")
    public ResponseEntity<List<Task>> getAllTasks(@RequestParam(required = false) String title) {
        try {
            List<Task> tasks = new ArrayList<Task>();

            taskRepository.findByTitleContaining(title).forEach(tasks::add);

            if (tasks.isEmpty()) {
                logger.info("Получение всех Task по " + title);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            logger.info("Получение всех Task по " + title);
            return new ResponseEntity<>(tasks, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Ошибка 500 - INTERNAL_SERVER_ERROR");
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable("id") long id) {
        Optional<Task> taskData = taskRepository.findById(id);

        if (taskData.isPresent()) {
            logger.info("Получение Task по " + id);
            return new ResponseEntity<>(taskData.get(), HttpStatus.OK);
        } else {
            logger.error("Ошибка 404 - NOT_FOUND");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/tasks")
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        try {
            Task _task = taskRepository
                    .save(new Task(task.getTitle(), task.getDescription()));
            logger.info("Создание Task");
            return new ResponseEntity<>(_task, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Ошибка 500 - INTERNAL_SERVER_ERROR");
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/tasks/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable("id") long id, @RequestBody Task task) {
        Optional<Task> taskData = taskRepository.findById(id);

        if (taskData.isPresent()) {
            Task _task = taskData.get();
            _task.setTitle(task.getTitle());
            _task.setDescription(task.getDescription());

            logger.info("Обновление Task по " + id);
            return new ResponseEntity<>(taskRepository.save(_task), HttpStatus.OK);
        } else {
            logger.error("Ошибка 404 - NOT_FOUND");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<HttpStatus> deleteTask(@PathVariable("id") long id) {
        try {
            taskRepository.deleteById(id);
            logger.info("Удаление Task по " + id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            logger.error("Ошибка 500 - INTERNAL_SERVER_ERROR");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/tasks")
    public ResponseEntity<HttpStatus> deleteAllTasks() {
        try {
            taskRepository.deleteAll();
            logger.info("Удаление всех Task");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            logger.error("Ошибка 500 - INTERNAL_SERVER_ERROR");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }



}

