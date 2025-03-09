package ru.efimov.DiplomFirst.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.efimov.DiplomFirst.entity.Enter;
import ru.efimov.DiplomFirst.entity.ProfileAnalyze;
import ru.efimov.DiplomFirst.repository.EnterRepository;
import ru.efimov.DiplomFirst.repository.ProfileAnalyzeRepository;
import ru.efimov.DiplomFirst.repository.StudentRepository;

import java.util.List;

@CrossOrigin(origins = "http://localhost:8083")
@RestController
@RequestMapping("/api")
public class ProfileAnalyzeController {
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ProfileAnalyzeRepository profileAnalyzeRepository;





    @GetMapping("/profileAnalyze/{id}")
    public ResponseEntity<ProfileAnalyze> getprofileAnalyzeByStudentId(@PathVariable(value = "id") Long id) {
        ProfileAnalyze profileAnalyze = profileAnalyzeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found ProfileAnalyze with id = " + id));

        return new ResponseEntity<>(profileAnalyze, HttpStatus.OK);
    }

    @PostMapping("/students/{studentId}/profileAnalyzes")
    public ResponseEntity<ProfileAnalyze> createProfileAnalyze(@PathVariable(value = "studentId") Long studentId,
                                             @RequestBody ProfileAnalyze profileAnalyzeRequest) {
        ProfileAnalyze profileAnalyze = studentRepository.findById(studentId).map(student -> {
            profileAnalyzeRequest.setStudent(student);
            return profileAnalyzeRepository.save(profileAnalyzeRequest);
        }).orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + studentId));

        return new ResponseEntity<>(profileAnalyze, HttpStatus.CREATED);
    }

    @PutMapping("/profileAnalyzes/{id}")
    public ResponseEntity<ProfileAnalyze> updateProfileAnalyze(@PathVariable("id") long id, @RequestBody ProfileAnalyze profileAnalyzeRequest) {
        ProfileAnalyze profileAnalyze = profileAnalyzeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProfileAnalyzeId " + id + "not found"));

        profileAnalyze.setLast_updates(profileAnalyzeRequest.getLast_updates());
        profileAnalyze.setCreated_date(profileAnalyzeRequest.getCreated_date());

        return new ResponseEntity<>(profileAnalyzeRepository.save(profileAnalyze), HttpStatus.OK);
    }

    @DeleteMapping("/profileAnalyzes/{id}")
    public ResponseEntity<HttpStatus> deleteProfileAnalyze(@PathVariable("id") long id) {
        profileAnalyzeRepository.deleteById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/students/{studentId}/profileAnalyzes")
    public ResponseEntity<List<ProfileAnalyze>> deleteAllprofileAnalyzesOfStudent(@PathVariable(value = "studentId") Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Not found Student with id = " + studentId);
        }

        profileAnalyzeRepository.deleteByStudentId(studentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}


