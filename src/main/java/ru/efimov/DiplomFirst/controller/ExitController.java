package ru.efimov.DiplomFirst.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.efimov.DiplomFirst.entity.Enter;
import ru.efimov.DiplomFirst.entity.Exit;
import ru.efimov.DiplomFirst.repository.EnterRepository;
import ru.efimov.DiplomFirst.repository.ExitRepository;
import ru.efimov.DiplomFirst.repository.StudentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:8083")
@RestController
@RequestMapping("/api")
public class ExitController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ExitRepository exitRepository;

    @GetMapping("/students/{studentId}/exits")
    public ResponseEntity<List<Exit>> getAllExitsByStudentId(@PathVariable(value = "studentId") Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Not found Student with id = " + studentId);
        }

        List<Exit> exits = exitRepository.findByStudentId(studentId);
        return new ResponseEntity<>(exits, HttpStatus.OK);
    }

    @GetMapping("/exits/{id}")
    public ResponseEntity<Exit> getExitsByStudentId(@PathVariable(value = "id") Long id) {
        Exit exit = exitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Exit with id = " + id));

        return new ResponseEntity<>(exit, HttpStatus.OK);
    }

    @PostMapping("/students/{studentId}/exits")
    public ResponseEntity<Exit> createExit(@PathVariable(value = "studentId") Long studentId,
                                             @RequestBody Exit exitRequest) {
        Exit exit = studentRepository.findById(studentId).map(student -> {
            exitRequest.setStudent(student);
            return exitRepository.save(exitRequest);
        }).orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + studentId));

        return new ResponseEntity<>(exit, HttpStatus.CREATED);
    }

    @PutMapping("/exits/{id}")
    public ResponseEntity<Exit> updateExit(@PathVariable("id") long id, @RequestBody Exit exitRequest) {
        Exit exit = exitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ExitId " + id + "not found"));

        exit.setDate_of_exit(exitRequest.getDate_of_exit());

        return new ResponseEntity<>(exitRepository.save(exit), HttpStatus.OK);
    }

    @DeleteMapping("/exits/{id}")
    public ResponseEntity<HttpStatus> deleteExit(@PathVariable("id") long id) {
        exitRepository.deleteById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/students/{studentId}/exits")
    public ResponseEntity<List<Exit>> deleteAllExitsOfStudent(@PathVariable(value = "studentId") Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Not found Student with id = " + studentId);
        }

        exitRepository.deleteByStudentId(studentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
