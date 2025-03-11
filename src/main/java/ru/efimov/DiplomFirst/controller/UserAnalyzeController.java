package ru.efimov.DiplomFirst.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.efimov.DiplomFirst.entity.ProfileAnalyze;
import ru.efimov.DiplomFirst.entity.UserAnalyze;
import ru.efimov.DiplomFirst.repository.ProfileAnalyzeRepository;
import ru.efimov.DiplomFirst.repository.StudentRepository;
import ru.efimov.DiplomFirst.repository.UserAnalyzeRepository;

import java.util.List;

@CrossOrigin(origins = "http://localhost:8083")
@RestController
@RequestMapping("/api")
public class UserAnalyzeController {
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserAnalyzeRepository userAnalyzeRepository;





    @GetMapping("/userAnalyze/{id}")
    public ResponseEntity<UserAnalyze> getuserAnalyzeByStudentId(@PathVariable(value = "id") Long id) {
        UserAnalyze userAnalyze = userAnalyzeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found UserAnalyze with id = " + id));

        return new ResponseEntity<>(userAnalyze, HttpStatus.OK);
    }

    @PostMapping("/students/{studentId}/userAnalyzes")
    public ResponseEntity<UserAnalyze> createUserAnalyze(@PathVariable(value = "studentId") Long studentId,
                                                               @RequestBody UserAnalyze userAnalyzeRequest) {
        UserAnalyze userAnalyze = studentRepository.findById(studentId).map(student -> {
            userAnalyzeRequest.setStudent(student);
            return userAnalyzeRepository.save(userAnalyzeRequest);
        }).orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + studentId));

        return new ResponseEntity<>(userAnalyze, HttpStatus.CREATED);
    }

    @PutMapping("/userAnalyzes/{id}")
    public ResponseEntity<UserAnalyze> updateUserAnalyze(@PathVariable("id") long id, @RequestBody UserAnalyze userAnalyzeRequest) {
        UserAnalyze userAnalyze = userAnalyzeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserAnalyzeId " + id + "not found"));

        userAnalyze.setAtribute_id(userAnalyzeRequest.getAtribute_id());
        userAnalyze.setCharacteristic(userAnalyzeRequest.getCharacteristic());
        userAnalyze.setValue(userAnalyzeRequest.getValue());

        return new ResponseEntity<>(userAnalyzeRepository.save(userAnalyze), HttpStatus.OK);
    }

    @DeleteMapping("/userAnalyzes/{id}")
    public ResponseEntity<HttpStatus> deleteUserAnalyze(@PathVariable("id") long id) {
        userAnalyzeRepository.deleteById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/students/{studentId}/userAnalyzes")
    public ResponseEntity<List<UserAnalyze>> deleteAlluserAnalyzesOfStudent(@PathVariable(value = "studentId") Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Not found Student with id = " + studentId);
        }

        userAnalyzeRepository.deleteByStudentId(studentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

