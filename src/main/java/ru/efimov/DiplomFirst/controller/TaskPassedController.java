package ru.efimov.DiplomFirst.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.efimov.DiplomFirst.entity.*;
import ru.efimov.DiplomFirst.repository.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:8083")
@RestController
@RequestMapping("/api")
public class TaskPassedController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskPassedRepository taskPassedRepository;

    @GetMapping("/tasks/{taskId}/taskPassed")
    public ResponseEntity<List<TaskPassed>> getAllTasksPassedByTaskId(@PathVariable(value = "taskId") Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new ResourceNotFoundException("Not found Task with id = " + taskId);
        }

        List<TaskPassed> tasksPassed = taskPassedRepository.findByTaskId(taskId);
        return new ResponseEntity<>(tasksPassed, HttpStatus.OK);
    }

    @GetMapping("/students/{studentId}/taskPassed")
    public ResponseEntity<List<TaskPassed>> getAllTasksPassedByStudentId(@PathVariable(value = "studentId") Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Not found Student with id = " + studentId);
        }

        List<TaskPassed> taskPasseds = taskPassedRepository.findByStudentId(studentId);
        return new ResponseEntity<>(taskPasseds, HttpStatus.OK);
    }

    @GetMapping("/tasksPassed/{id}")
    public ResponseEntity<TaskPassed> getTasksPassedByStudentId(@PathVariable(value = "id") Long id) {
        TaskPassed taskPassed = taskPassedRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found TaskPassed with id = " + id));

        return new ResponseEntity<>(taskPassed, HttpStatus.OK);
    }

    @PostMapping("/students/{studentId}/tasksPassed/{taskId}")
    public ResponseEntity<TaskPassed> createTaskPassed(@PathVariable(value = "studentId") Long studentId,
                                                           @PathVariable(value = "taskId") Long taskId,
                                                           @RequestBody TaskPassed taskPassedRequest) {
        Optional<Student> optionalStudent = studentRepository.findById(studentId);
        Optional<Task> optionalTask = taskRepository.findById(taskId);
        Student student = null;
        Task task = null;
        if (optionalStudent.isPresent()){
            student = optionalStudent.get();
        }
        else{
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (optionalTask.isPresent()){
            task = optionalTask.get();
        }
        else{
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }


        TaskPassed taskPassed = new TaskPassed(student, task, taskPassedRequest.getDate_of_passed());

        TaskPassed _taskPassed = taskPassedRepository.save(taskPassed);

        return new ResponseEntity<>(_taskPassed, HttpStatus.CREATED);
    }

    @PutMapping("/tasksPassed/{id}")
    public ResponseEntity<TaskPassed> updateTaskPassed(@PathVariable("id") long id, @RequestBody TaskPassed taskPassedRequest) {
        TaskPassed taskPassed = taskPassedRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TaskPassedId " + id + "not found"));

        taskPassed.setDate_of_passed(taskPassedRequest.getDate_of_passed());

        return new ResponseEntity<>(taskPassedRepository.save(taskPassed), HttpStatus.OK);
    }

    @DeleteMapping("/tasksPassed/{id}")
    public ResponseEntity<HttpStatus> deleteTaskPassed(@PathVariable("id") long id) {
        taskPassedRepository.deleteById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/students/{studentId}/tasksPassed")
    public ResponseEntity<List<TaskPassed>> deleteAllTasksPassedOfStudent(@PathVariable(value = "studentId") Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Not found Student with id = " + studentId);
        }

        taskPassedRepository.deleteByStudentId(studentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/tasks/{taskId}/tasksPassed")
    public ResponseEntity<List<TaskPassed>> deleteAllTasksPassedOfTask(@PathVariable(value = "taskId") Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new ResourceNotFoundException("Not found Task with id = " + taskId);
        }

        taskPassedRepository.deleteByTaskId(taskId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}



