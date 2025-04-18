package ru.efimov.DiplomFirst.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.efimov.DiplomFirst.domain.JwtAuthentication;
import ru.efimov.DiplomFirst.entity.Student;
import ru.efimov.DiplomFirst.repository.StudentRepository;
import ru.efimov.DiplomFirst.service.JwtProvider;
import ru.efimov.DiplomFirst.service.UserService;
import io.jsonwebtoken.Claims;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@CrossOrigin(origins = "http://localhost:8083")
@RestController
@RequestMapping("/api")
public class StudentController {

    @Autowired
    StudentRepository studentRepository;

    private static final Logger logger = LogManager.getLogger(StudentController.class);

    private UserService service;

    public StudentController(UserService service) {
        this.service = service;
    }

    @GetMapping("/students")
    public ResponseEntity<List<Student>> getAllStudents() {
        List<Student> students = new ArrayList<Student>();


        studentRepository.findAll().forEach(students::add);

        if (students.isEmpty()) {
            logger.info("Получение всех объектов Student");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        logger.info("Получение всех объектов Student");
        return new ResponseEntity<>(students, HttpStatus.OK);
    }

    @GetMapping("/students/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable("id") long id) {


        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + id));


        if (!SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals(student.getUsername())){
            logger.error("Ошибка 403 - FORBIDDEN");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        logger.info("Получение Student по" + id);
        return new ResponseEntity<>(student, HttpStatus.OK);
    }

    @PostMapping("/students")
    public ResponseEntity<Student> createStudent(@RequestBody Student student) {
        Student _student = studentRepository.save(new Student(student.getUsername(), student.getPassword(), student.getDate_of_registration()));
        logger.info("Создание Student" );
        return new ResponseEntity<>(_student, HttpStatus.CREATED);
    }

    @PutMapping("/students/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable("id") long id, @RequestBody Student student) {
        Student _student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + id));

        if (!SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals(_student.getUsername())){
            logger.error("Ошибка 403 - FORBIDDEN");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        _student.setUsername(student.getUsername());
        _student.setPassword(student.getPassword());
        _student.setDate_of_registration(student.getDate_of_registration());

        logger.info("Обновление Student по" + id);

        return new ResponseEntity<>(studentRepository.save(_student), HttpStatus.OK);
    }

    @DeleteMapping("/students/{id}")
    public ResponseEntity<HttpStatus> deleteStudent(@PathVariable("id") long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + id));

        if (!SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals(student.getUsername())){
            logger.error("Ошибка 403 - FORBIDDEN");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        studentRepository.deleteById(id);

        logger.info("Удаление Student по" + id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/students")
    public ResponseEntity<HttpStatus> deleteAllStudents() {
        studentRepository.deleteAll();

        logger.info("Удаление всех Student" );

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}
