package ru.efimov.DiplomFirst.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.efimov.DiplomFirst.entity.*;
import ru.efimov.DiplomFirst.repository.*;

import java.util.ArrayList;
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

        List<TaskPassed> taskPassedsCurStudent = new ArrayList<>();
        for (TaskPassed t1 : tasksPassed){
            Student student1 = studentRepository.findById(t1.getStudent().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + t1.getStudent().getId()));

            if (SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals(student1.getUsername())){
                taskPassedsCurStudent.add(t1);
            }
        }


        return new ResponseEntity<>(taskPassedsCurStudent, HttpStatus.OK);
    }

    @GetMapping("/students/{studentId}/taskPassed")
    public ResponseEntity<List<TaskPassed>> getAllTasksPassedByStudentId(@PathVariable(value = "studentId") Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Not found Student with id = " + studentId);
        }
        Student student1 = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + studentId));

        if (!SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals(student1.getUsername())){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }


        List<TaskPassed> taskPasseds = taskPassedRepository.findByStudentId(studentId);
        return new ResponseEntity<>(taskPasseds, HttpStatus.OK);
    }

    @GetMapping("/tasksPassed/{id}")
    public ResponseEntity<TaskPassed> getTasksPassedByStudentId(@PathVariable(value = "id") Long id) {
        TaskPassed taskPassed = taskPassedRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found TaskPassed with id = " + id));

        Student student1 = studentRepository.findById(taskPassed.getStudent().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + taskPassed.getStudent().getId()));

        if (!SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals(student1.getUsername())){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }


        return new ResponseEntity<>(taskPassed, HttpStatus.OK);
    }

    @PostMapping("/students/{studentId}/tasksPassed/{taskId}")
    public ResponseEntity<TaskPassed> createTaskPassed(@PathVariable(value = "studentId") Long studentId,
                                                           @PathVariable(value = "taskId") Long taskId,
                                                           @RequestBody TaskPassed taskPassedRequest) {
        Student student1 = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + studentId));

        if (!SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals(student1.getUsername())){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }


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



        Student student1 = studentRepository.findById(taskPassed.getStudent().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + taskPassed.getStudent().getId()));

        if (!SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals(student1.getUsername())){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        taskPassed.setDate_of_passed(taskPassedRequest.getDate_of_passed());

        return new ResponseEntity<>(taskPassedRepository.save(taskPassed), HttpStatus.OK);
    }

    @DeleteMapping("/tasksPassed/{id}")
    public ResponseEntity<HttpStatus> deleteTaskPassed(@PathVariable("id") long id) {
        TaskPassed taskPassed = taskPassedRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found TaskPassed with id = " + id));

        Student student1 = studentRepository.findById(taskPassed.getStudent().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + taskPassed.getStudent().getId()));

        if (!SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals(student1.getUsername())){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        taskPassedRepository.deleteById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/students/{studentId}/tasksPassed")
    public ResponseEntity<List<TaskPassed>> deleteAllTasksPassedOfStudent(@PathVariable(value = "studentId") Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Not found Student with id = " + studentId);
        }
        Student student1 = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + studentId));

        if (!SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals(student1.getUsername())){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }


        taskPassedRepository.deleteByStudentId(studentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/tasks/{taskId}/tasksPassed")
    public ResponseEntity<List<TaskPassed>> deleteAllTasksPassedOfTask(@PathVariable(value = "taskId") Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new ResourceNotFoundException("Not found Task with id = " + taskId);
        }

        TaskPassed taskPassed = taskPassedRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found TaskPassed with id = " + taskId));

        Student student1 = studentRepository.findById(taskPassed.getStudent().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + taskPassed.getStudent().getId()));

        if (!SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals(student1.getUsername())){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        taskPassedRepository.deleteByTaskId(taskId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}



