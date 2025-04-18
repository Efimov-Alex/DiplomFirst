package ru.efimov.DiplomFirst.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.efimov.DiplomFirst.entity.Enter;
import ru.efimov.DiplomFirst.entity.Material;
import ru.efimov.DiplomFirst.entity.Student;
import ru.efimov.DiplomFirst.repository.EnterRepository;
import ru.efimov.DiplomFirst.repository.MaterialRepository;
import ru.efimov.DiplomFirst.repository.StudentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@CrossOrigin(origins = "http://localhost:8083")
@RestController
@RequestMapping("/api")
public class EnterController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private EnterRepository enterRepository;

    private static final Logger logger = LogManager.getLogger(EnterController.class);

    @GetMapping("/students/{studentId}/enters")
    public ResponseEntity<List<Enter>> getAllEntersByStudentId(@PathVariable(value = "studentId") Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            logger.error("Not found Student with id = " + studentId);
            throw new ResourceNotFoundException("Not found Student with id = " + studentId);
        }
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + studentId));

        if (!SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals(student.getUsername())){
            logger.error("Ошибка 403 - FORBIDDEN");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }


        List<Enter> enters = enterRepository.findByStudentId(studentId);
        logger.info("Получение всех объектов Enter по " + studentId);
        return new ResponseEntity<>(enters, HttpStatus.OK);
    }

    @GetMapping("/enters/{id}")
    public ResponseEntity<Enter> getEntersByStudentId(@PathVariable(value = "id") Long id) {
        Enter enter = enterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Enter with id = " + id));

        Student student = studentRepository.findById(enter.getStudent().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + enter.getStudent().getId()));

        if (!SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals(student.getUsername())){
            logger.error("Ошибка 403 - FORBIDDEN");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        logger.info("Получение всех объектов Enter по " + id);
        return new ResponseEntity<>(enter, HttpStatus.OK);
    }

    @PostMapping("/students/{studentId}/enters")
    public ResponseEntity<Enter> createEnter(@PathVariable(value = "studentId") Long studentId,
                                                 @RequestBody Enter enterRequest) {
        Student student1 = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + studentId));

        if (!SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals(student1.getUsername())){
            logger.error("Ошибка 403 - FORBIDDEN");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }


        Enter enter = studentRepository.findById(studentId).map(student -> {
            enterRequest.setStudent(student);
            return enterRepository.save(enterRequest);
        }).orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + studentId));

        logger.info("Создание объекта Enter по " + studentId);
        return new ResponseEntity<>(enter, HttpStatus.CREATED);
    }

    @PutMapping("/enters/{id}")
    public ResponseEntity<Enter> updateEnter(@PathVariable("id") long id, @RequestBody Enter enterRequest) {
        Enter enter = enterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EnterId " + id + "not found"));

        Student student = studentRepository.findById(enter.getStudent().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + enter.getStudent().getId()));

        if (!SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals(student.getUsername())){
            logger.error("Ошибка 403 - FORBIDDEN");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        enter.setDate_of_enter(enterRequest.getDate_of_enter());

        logger.info("Обновление объекта Enter по " + id);
        return new ResponseEntity<>(enterRepository.save(enter), HttpStatus.OK);
    }

    @DeleteMapping("/enters/{id}")
    public ResponseEntity<HttpStatus> deleteEnter(@PathVariable("id") long id) {
        Enter enter = enterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EnterId " + id + "not found"));

        Student student = studentRepository.findById(enter.getStudent().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + enter.getStudent().getId()));

        if (!SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals(student.getUsername())){
            logger.error("Ошибка 403 - FORBIDDEN");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        enterRepository.deleteById(id);

        logger.info("Удаление объекта Enter по " + id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/students/{studentId}/enters")
    public ResponseEntity<List<Enter>> deleteAllEntersOfStudent(@PathVariable(value = "studentId") Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            logger.error("Not found Student with id = " + studentId);
            throw new ResourceNotFoundException("Not found Student with id = " + studentId);
        }
        Student student1 = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + studentId));

        if (!SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals(student1.getUsername())){
            logger.error("Ошибка 403 - FORBIDDEN");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        enterRepository.deleteByStudentId(studentId);
        logger.info("Удаление всех объектов Enter по " + studentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
