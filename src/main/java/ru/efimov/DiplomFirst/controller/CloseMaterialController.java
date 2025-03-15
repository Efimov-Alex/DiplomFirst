package ru.efimov.DiplomFirst.controller;

import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.efimov.DiplomFirst.entity.CloseMaterial;
import ru.efimov.DiplomFirst.entity.Material;
import ru.efimov.DiplomFirst.entity.OpenMaterial;
import ru.efimov.DiplomFirst.entity.Student;
import ru.efimov.DiplomFirst.repository.CloseMaterialRepository;
import ru.efimov.DiplomFirst.repository.MaterialRepository;
import ru.efimov.DiplomFirst.repository.OpenMaterialRepository;
import ru.efimov.DiplomFirst.repository.StudentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@CrossOrigin(origins = "http://localhost:8083")
@RestController
@RequestMapping("/api")
public class CloseMaterialController {
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private CloseMaterialRepository closeMaterialRepository;

    @GetMapping("/materials/{materialId}/closeMaterials")
    public ResponseEntity<List<CloseMaterial>> getAllCloseMaterialsByMaterialId(@PathVariable(value = "materialId") Long materialId) {
        if (!materialRepository.existsById(materialId)) {
            throw new ResourceNotFoundException("Not found Material with id = " + materialId);
        }

        List<CloseMaterial> closeMaterials = closeMaterialRepository.findByMaterialId(materialId);

        List<CloseMaterial> closeMaterialsCurStudent = new ArrayList<>();
        for (CloseMaterial c1 : closeMaterials){
            Student student1 = studentRepository.findById(c1.getStudent().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + c1.getStudent().getId()));

            if (SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals(student1.getUsername())){
                closeMaterialsCurStudent.add(c1);
            }
        }
        return new ResponseEntity<>(closeMaterialsCurStudent, HttpStatus.OK);
    }

    @GetMapping("/students/{studentId}/closeMaterials")
    public ResponseEntity<List<CloseMaterial>> getAllCloseMaterialsByStudentId(@PathVariable(value = "studentId") Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Not found Student with id = " + studentId);
        }
        Student student1 = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + studentId));

        if (!SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals(student1.getUsername())){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }


        List<CloseMaterial> closeMaterials = closeMaterialRepository.findByStudentId(studentId);
        return new ResponseEntity<>(closeMaterials, HttpStatus.OK);
    }

    @GetMapping("/closeMaterials/{id}")
    public ResponseEntity<CloseMaterial> getCloseMaterialsByStudentId(@PathVariable(value = "id") Long id) {
        CloseMaterial closeMaterial = closeMaterialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found CloseMaterial with id = " + id));

        Student student1 = studentRepository.findById(closeMaterial.getStudent().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + closeMaterial.getStudent().getId()));

        if (!SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals(student1.getUsername())){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<>(closeMaterial, HttpStatus.OK);
    }

    @PostMapping("/students/{studentId}/closeMaterials/{materialId}")
    public ResponseEntity<CloseMaterial> createCloseMaterial(@PathVariable(value = "studentId") Long studentId,
                                                           @PathVariable(value = "materialId") Long materialId,
                                                           @RequestBody CloseMaterial closeMaterialRequest) {
        Student student1 = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + studentId));

        if (!SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals(student1.getUsername())){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }


        Optional<Student> optionalStudent = studentRepository.findById(studentId);
        Optional<Material> optionalMaterial = materialRepository.findById(materialId);
        Student student = null;
        Material material = null;
        if (optionalStudent.isPresent()){
            student = optionalStudent.get();
        }
        else{
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (optionalMaterial.isPresent()){
            material = optionalMaterial.get();
        }
        else{
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }


        CloseMaterial closeMaterial = new CloseMaterial(student, material, closeMaterialRequest.getDate_of_close());

        CloseMaterial _closeMaterial = closeMaterialRepository.save(closeMaterial);

        return new ResponseEntity<>(_closeMaterial, HttpStatus.CREATED);
    }

    @PutMapping("/closeMaterials/{id}")
    public ResponseEntity<CloseMaterial> updateCloseMaterial(@PathVariable("id") long id, @RequestBody CloseMaterial closeMaterialRequest) {
        CloseMaterial closeMaterial = closeMaterialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CloseMaterialId " + id + "not found"));

        Student student1 = studentRepository.findById(closeMaterial.getStudent().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + closeMaterial.getStudent().getId()));

        if (!SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals(student1.getUsername())){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        closeMaterial.setDate_of_close(closeMaterialRequest.getDate_of_close());

        return new ResponseEntity<>(closeMaterialRepository.save(closeMaterial), HttpStatus.OK);
    }

    @DeleteMapping("/closeMaterials/{id}")
    public ResponseEntity<HttpStatus> deleteCloseMaterial(@PathVariable("id") long id) {
        CloseMaterial closeMaterial = closeMaterialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CloseMaterialId " + id + "not found"));

        Student student1 = studentRepository.findById(closeMaterial.getStudent().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + closeMaterial.getStudent().getId()));

        if (!SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals(student1.getUsername())){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        closeMaterialRepository.deleteById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/students/{studentId}/closeMaterials")
    public ResponseEntity<List<CloseMaterial>> deleteAllCloseMaterialsOfStudent(@PathVariable(value = "studentId") Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Not found Student with id = " + studentId);
        }
        Student student1 = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + studentId));

        if (!SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals(student1.getUsername())){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }


        closeMaterialRepository.deleteByStudentId(studentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/materials/{materialId}/closeMaterials")
    public ResponseEntity<List<CloseMaterial>> deleteAllCloseMaterialsOfMaterial(@PathVariable(value = "materialId") Long materialId) {
        if (!materialRepository.existsById(materialId)) {
            throw new ResourceNotFoundException("Not found Material with id = " + materialId);
        }

        closeMaterialRepository.deleteByMaterialId(materialId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}


