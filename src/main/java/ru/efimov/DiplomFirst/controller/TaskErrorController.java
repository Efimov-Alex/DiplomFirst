package ru.efimov.DiplomFirst.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.efimov.DiplomFirst.entity.Student;
import ru.efimov.DiplomFirst.entity.Task;
import ru.efimov.DiplomFirst.entity.TaskError;
import ru.efimov.DiplomFirst.entity.TaskPassed;
import ru.efimov.DiplomFirst.repository.StudentRepository;
import ru.efimov.DiplomFirst.repository.TaskErrorRepository;
import ru.efimov.DiplomFirst.repository.TaskPassedRepository;
import ru.efimov.DiplomFirst.repository.TaskRepository;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:8083")
@RestController
@RequestMapping("/api")
public class TaskErrorController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskErrorRepository taskErrorRepository;

    @GetMapping("/tasks/{taskId}/taskError")
    public ResponseEntity<List<TaskError>> getAllTasksErrorByTaskId(@PathVariable(value = "taskId") Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new ResourceNotFoundException("Not found Task with id = " + taskId);
        }

        List<TaskError> taskErrors = taskErrorRepository.findByTaskId(taskId);
        return new ResponseEntity<>(taskErrors, HttpStatus.OK);
    }

    @GetMapping("/students/{studentId}/taskError")
    public ResponseEntity<List<TaskError>> getAllTasksErrorByStudentId(@PathVariable(value = "studentId") Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Not found Student with id = " + studentId);
        }

        List<TaskError> taskErrors = taskErrorRepository.findByStudentId(studentId);
        return new ResponseEntity<>(taskErrors, HttpStatus.OK);
    }

    @GetMapping("/taskError/{id}")
    public ResponseEntity<TaskError> getTasksErrorByStudentId(@PathVariable(value = "id") Long id) {
        TaskError taskError = taskErrorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found TaskError with id = " + id));

        return new ResponseEntity<>(taskError, HttpStatus.OK);
    }

    @PostMapping("/students/{studentId}/taskError/{taskId}")
    public ResponseEntity<TaskError> createTaskError(@PathVariable(value = "studentId") Long studentId,
                                                       @PathVariable(value = "taskId") Long taskId,
                                                       @RequestBody TaskError taskErrorRequest) {
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


        TaskError taskError = new TaskError(student, task, taskErrorRequest.getDate_of_error(), taskErrorRequest.getCount_errors());

        TaskError _taskError = taskErrorRepository.save(taskError);

        return new ResponseEntity<>(_taskError, HttpStatus.CREATED);
    }

    @PutMapping("/taskError/{id}")
    public ResponseEntity<TaskError> updateTaskError(@PathVariable("id") long id, @RequestBody TaskError taskErrorRequest) {
        TaskError taskError = taskErrorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TaskErrorId " + id + "not found"));

        taskError.setDate_of_error(taskErrorRequest.getDate_of_error());
        taskError.setCount_errors(taskErrorRequest.getCount_errors());

        return new ResponseEntity<>(taskErrorRepository.save(taskError), HttpStatus.OK);
    }

    @DeleteMapping("/taskError/{id}")
    public ResponseEntity<HttpStatus> deleteTaskError(@PathVariable("id") long id) {
        taskErrorRepository.deleteById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/students/{studentId}/taskError")
    public ResponseEntity<List<TaskError>> deleteAllTasksErrorOfStudent(@PathVariable(value = "studentId") Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Not found Student with id = " + studentId);
        }

        taskErrorRepository.deleteByStudentId(studentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/tasks/{taskId}/taskError")
    public ResponseEntity<List<TaskError>> deleteAllTasksErrorOfTask(@PathVariable(value = "taskId") Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new ResourceNotFoundException("Not found Task with id = " + taskId);
        }

        taskErrorRepository.deleteByTaskId(taskId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

