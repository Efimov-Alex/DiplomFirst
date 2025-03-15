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


@CrossOrigin(origins = "http://localhost:8083")
@RestController
@RequestMapping("/api")
public class EnterController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private EnterRepository enterRepository;

    @GetMapping("/students/{studentId}/enters")
    public ResponseEntity<List<Enter>> getAllEntersByStudentId(@PathVariable(value = "studentId") Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Not found Student with id = " + studentId);
        }
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + studentId));

        if (!SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals(student.getUsername())){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }


        List<Enter> enters = enterRepository.findByStudentId(studentId);
        return new ResponseEntity<>(enters, HttpStatus.OK);
    }

    @GetMapping("/enters/{id}")
    public ResponseEntity<Enter> getEntersByStudentId(@PathVariable(value = "id") Long id) {
        Enter enter = enterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Enter with id = " + id));

        Student student = studentRepository.findById(enter.getStudent().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + enter.getStudent().getId()));

        if (!SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals(student.getUsername())){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<>(enter, HttpStatus.OK);
    }

    @PostMapping("/students/{studentId}/enters")
    public ResponseEntity<Enter> createEnter(@PathVariable(value = "studentId") Long studentId,
                                                 @RequestBody Enter enterRequest) {
        Student student1 = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + studentId));

        if (!SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals(student1.getUsername())){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }


        Enter enter = studentRepository.findById(studentId).map(student -> {
            enterRequest.setStudent(student);
            return enterRepository.save(enterRequest);
        }).orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + studentId));

        return new ResponseEntity<>(enter, HttpStatus.CREATED);
    }

    @PutMapping("/enters/{id}")
    public ResponseEntity<Enter> updateEnter(@PathVariable("id") long id, @RequestBody Enter enterRequest) {
        Enter enter = enterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EnterId " + id + "not found"));

        Student student = studentRepository.findById(enter.getStudent().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + enter.getStudent().getId()));

        if (!SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals(student.getUsername())){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        enter.setDate_of_enter(enterRequest.getDate_of_enter());

        return new ResponseEntity<>(enterRepository.save(enter), HttpStatus.OK);
    }

    @DeleteMapping("/enters/{id}")
    public ResponseEntity<HttpStatus> deleteEnter(@PathVariable("id") long id) {
        Enter enter = enterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EnterId " + id + "not found"));

        Student student = studentRepository.findById(enter.getStudent().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + enter.getStudent().getId()));

        if (!SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals(student.getUsername())){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        enterRepository.deleteById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/students/{studentId}/enters")
    public ResponseEntity<List<Enter>> deleteAllEntersOfStudent(@PathVariable(value = "studentId") Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Not found Student with id = " + studentId);
        }
        Student student1 = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + studentId));

        if (!SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals(student1.getUsername())){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        enterRepository.deleteByStudentId(studentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
