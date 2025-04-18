package ru.efimov.DiplomFirst.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.efimov.DiplomFirst.entity.Enter;
import ru.efimov.DiplomFirst.entity.Exit;
import ru.efimov.DiplomFirst.entity.Student;
import ru.efimov.DiplomFirst.repository.EnterRepository;
import ru.efimov.DiplomFirst.repository.ExitRepository;
import ru.efimov.DiplomFirst.repository.StudentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@CrossOrigin(origins = "http://localhost:8083")
@RestController
@RequestMapping("/api")
public class ExitController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ExitRepository exitRepository;

    private static final Logger logger = LogManager.getLogger(ExitController.class);

    @GetMapping("/students/{studentId}/exits")
    public ResponseEntity<List<Exit>> getAllExitsByStudentId(@PathVariable(value = "studentId") Long studentId) {
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


        List<Exit> exits = exitRepository.findByStudentId(studentId);
        logger.info("Получение всех объектов Enter по " + studentId);
        return new ResponseEntity<>(exits, HttpStatus.OK);
    }

    @GetMapping("/exits/{id}")
    public ResponseEntity<Exit> getExitsByStudentId(@PathVariable(value = "id") Long id) {
        Exit exit = exitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Exit with id = " + id));

        Student student = studentRepository.findById(exit.getStudent().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + exit.getStudent().getId()));

        if (!SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals(student.getUsername())){
            logger.error("Ошибка 403 - FORBIDDEN");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        logger.info("Получение объекта Enter по " + id);
        return new ResponseEntity<>(exit, HttpStatus.OK);
    }

    @PostMapping("/students/{studentId}/exits")
    public ResponseEntity<Exit> createExit(@PathVariable(value = "studentId") Long studentId,
                                             @RequestBody Exit exitRequest) {
        Student student1 = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + studentId));

        if (!SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals(student1.getUsername())){
            logger.error("Ошибка 403 - FORBIDDEN");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }


        Exit exit = studentRepository.findById(studentId).map(student -> {
            exitRequest.setStudent(student);
            return exitRepository.save(exitRequest);
        }).orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + studentId));

        logger.info("Создание объекта Enter по " + studentId);
        return new ResponseEntity<>(exit, HttpStatus.CREATED);
    }

    @PutMapping("/exits/{id}")
    public ResponseEntity<Exit> updateExit(@PathVariable("id") long id, @RequestBody Exit exitRequest) {
        Exit exit = exitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ExitId " + id + "not found"));

        Student student = studentRepository.findById(exit.getStudent().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + exit.getStudent().getId()));

        if (!SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals(student.getUsername())){
            logger.error("Ошибка 403 - FORBIDDEN");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        exit.setDate_of_exit(exitRequest.getDate_of_exit());

        logger.info("Обновление объекта Enter по " + id);
        return new ResponseEntity<>(exitRepository.save(exit), HttpStatus.OK);
    }

    @DeleteMapping("/exits/{id}")
    public ResponseEntity<HttpStatus> deleteExit(@PathVariable("id") long id) {
        Exit exit = exitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ExitId " + id + "not found"));

        Student student = studentRepository.findById(exit.getStudent().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + exit.getStudent().getId()));

        if (!SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals(student.getUsername())){
            logger.error("Ошибка 403 - FORBIDDEN");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        exitRepository.deleteById(id);

        logger.info("Удаление объекта Enter по " + id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/students/{studentId}/exits")
    public ResponseEntity<List<Exit>> deleteAllExitsOfStudent(@PathVariable(value = "studentId") Long studentId) {
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



        exitRepository.deleteByStudentId(studentId);
        logger.info("Удаление всех Enter по " + studentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
