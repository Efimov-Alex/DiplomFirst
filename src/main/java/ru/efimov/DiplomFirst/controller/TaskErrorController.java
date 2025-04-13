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
public class TaskErrorController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskErrorRepository taskErrorRepository;

    @Autowired
    private UserAnalyzeRepository userAnalyzeRepository;

    @GetMapping("/tasks/{taskId}/taskError")
    public ResponseEntity<List<TaskError>> getAllTasksErrorByTaskId(@PathVariable(value = "taskId") Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new ResourceNotFoundException("Not found Task with id = " + taskId);
        }

        List<TaskError> taskErrors = taskErrorRepository.findByTaskId(taskId);

        List<TaskError> taskErrorsCurStudent = new ArrayList<>();
        for (TaskError t1 : taskErrors){
            Student student1 = studentRepository.findById(t1.getStudent().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + t1.getStudent().getId()));

            if (SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals(student1.getUsername())){
                taskErrorsCurStudent.add(t1);
            }
        }

        return new ResponseEntity<>(taskErrorsCurStudent, HttpStatus.OK);
    }

    @GetMapping("/students/{studentId}/taskError")
    public ResponseEntity<List<TaskError>> getAllTasksErrorByStudentId(@PathVariable(value = "studentId") Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Not found Student with id = " + studentId);
        }
        Student student1 = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + studentId));

        if (!SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals(student1.getUsername())){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }


        List<TaskError> taskErrors = taskErrorRepository.findByStudentId(studentId);
        return new ResponseEntity<>(taskErrors, HttpStatus.OK);
    }

    @GetMapping("/taskError/{id}")
    public ResponseEntity<TaskError> getTasksErrorByStudentId(@PathVariable(value = "id") Long id) {
        TaskError taskError = taskErrorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found TaskError with id = " + id));

        Student student1 = studentRepository.findById(taskError.getStudent().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + taskError.getStudent().getId()));

        if (!SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals(student1.getUsername())){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<>(taskError, HttpStatus.OK);
    }

    @PostMapping("/students/{studentId}/taskError/{taskId}")
    public ResponseEntity<TaskError> createTaskError(@PathVariable(value = "studentId") Long studentId,
                                                       @PathVariable(value = "taskId") Long taskId,
                                                       @RequestBody TaskError taskErrorRequest) {
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


        TaskError taskError = new TaskError(student, task, taskErrorRequest.getDate_of_error(), taskErrorRequest.getCount_errors());


        List<UserAnalyze> listUserAnalyze = userAnalyzeRepository.findByStudentId(studentId);
        UserAnalyze userAnalyzeCountErrors = null;
        for (UserAnalyze userAnalyze : listUserAnalyze){
            if (userAnalyze.getCharacteristic().contains("Колличество ошибок")){
                userAnalyzeCountErrors = userAnalyze;
            }
        }

        List<TaskError> listTaskErrorByUser = taskErrorRepository.findByStudentId(studentId);



        if (listTaskErrorByUser.size() >= 10){
            if ( 4 * taskError.getCount_errors() >= 3 * Double.parseDouble(userAnalyzeCountErrors.getValue()) && taskError.getCount_errors() <= 5 * Double.parseDouble(userAnalyzeCountErrors.getValue())){
                System.out.println("Значение в пределах нормы");
                TaskError _taskError = taskErrorRepository.save(taskError);
                return new ResponseEntity<>(_taskError, HttpStatus.CREATED);
            }
            else if (2 * taskError.getCount_errors() <  Double.parseDouble(userAnalyzeCountErrors.getValue()) || taskError.getCount_errors() > 3 * Double.parseDouble(userAnalyzeCountErrors.getValue())){
                System.out.println("Значение сильно отличаются, это другой человек.");
                return new ResponseEntity<>(taskError, HttpStatus.CREATED);
                //TaskError _taskError = taskErrorRepository.save(taskError);
            }
            else {
                System.out.println("Значение в отличаюся от нормальных, но не сильно.");
                TaskError _taskError = taskErrorRepository.save(taskError);
                return new ResponseEntity<>(_taskError, HttpStatus.CREATED);
            }
        }


        else {
            System.out.println("Мало данных, чтобы понять другой ли это пользователь.");
            TaskError _taskError = taskErrorRepository.save(taskError);
            return new ResponseEntity<>(_taskError, HttpStatus.CREATED);
        }

        //return new ResponseEntity<>(_taskError, HttpStatus.CREATED);
    }

    @PutMapping("/taskError/{id}")
    public ResponseEntity<TaskError> updateTaskError(@PathVariable("id") long id, @RequestBody TaskError taskErrorRequest) {
        TaskError taskError = taskErrorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TaskErrorId " + id + "not found"));

        Student student1 = studentRepository.findById(taskError.getStudent().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + taskError.getStudent().getId()));

        if (!SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals(student1.getUsername())){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }


        taskError.setDate_of_error(taskErrorRequest.getDate_of_error());
        taskError.setCount_errors(taskErrorRequest.getCount_errors());

        return new ResponseEntity<>(taskErrorRepository.save(taskError), HttpStatus.OK);
    }

    @DeleteMapping("/taskError/{id}")
    public ResponseEntity<HttpStatus> deleteTaskError(@PathVariable("id") long id) {
        TaskError taskError = taskErrorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TaskErrorId " + id + "not found"));

        Student student1 = studentRepository.findById(taskError.getStudent().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + taskError.getStudent().getId()));

        if (!SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals(student1.getUsername())){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        taskErrorRepository.deleteById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/students/{studentId}/taskError")
    public ResponseEntity<List<TaskError>> deleteAllTasksErrorOfStudent(@PathVariable(value = "studentId") Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Not found Student with id = " + studentId);
        }
        Student student1 = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + studentId));

        if (!SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals(student1.getUsername())){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }


        taskErrorRepository.deleteByStudentId(studentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/tasks/{taskId}/taskError")
    public ResponseEntity<List<TaskError>> deleteAllTasksErrorOfTask(@PathVariable(value = "taskId") Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new ResourceNotFoundException("Not found Task with id = " + taskId);
        }
        TaskError taskError = taskErrorRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("TaskErrorId " + taskId + "not found"));

        Student student1 = studentRepository.findById(taskError.getStudent().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + taskError.getStudent().getId()));

        if (!SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals(student1.getUsername())){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }


        taskErrorRepository.deleteByTaskId(taskId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

