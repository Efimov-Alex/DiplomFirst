package ru.efimov.DiplomFirst.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.efimov.DiplomFirst.entity.*;
import ru.efimov.DiplomFirst.repository.EnterRepository;
import ru.efimov.DiplomFirst.repository.MaterialRepository;
import ru.efimov.DiplomFirst.repository.OpenMaterialRepository;
import ru.efimov.DiplomFirst.repository.StudentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@CrossOrigin(origins = "http://localhost:8083")
@RestController
@RequestMapping("/api")
public class OpenMaterialController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private OpenMaterialRepository openMaterialRepository;

    @GetMapping("/materials/{materialId}/openMaterials")
    public ResponseEntity<List<OpenMaterial>> getAllOpenMaterialsByMaterialId(@PathVariable(value = "materialId") Long materialId) {
        if (!materialRepository.existsById(materialId)) {
            throw new ResourceNotFoundException("Not found Material with id = " + materialId);
        }

        List<OpenMaterial> openMaterials = openMaterialRepository.findByMaterialId(materialId);

        List<OpenMaterial> openMaterialsCurStudent = new ArrayList<>();
        for (OpenMaterial o1 : openMaterials){
            Student student1 = studentRepository.findById(o1.getStudent().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + o1.getStudent().getId()));

            if (SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals(student1.getUsername())){
                openMaterialsCurStudent.add(o1);
            }
        }

        return new ResponseEntity<>(openMaterialsCurStudent, HttpStatus.OK);
    }

    @GetMapping("/students/{studentId}/openMaterials")
    public ResponseEntity<List<OpenMaterial>> getAllOpenMaterialsByStudentId(@PathVariable(value = "studentId") Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Not found Student with id = " + studentId);
        }
        Student student1 = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + studentId));

        if (!SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals(student1.getUsername())){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }


        List<OpenMaterial> openMaterials = openMaterialRepository.findByStudentId(studentId);
        return new ResponseEntity<>(openMaterials, HttpStatus.OK);
    }

    @GetMapping("/openMaterials/{id}")
    public ResponseEntity<OpenMaterial> getOpenMaterialsByStudentId(@PathVariable(value = "id") Long id) {
        OpenMaterial openMaterial = openMaterialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found OpenMaterial with id = " + id));

        Student student1 = studentRepository.findById(openMaterial.getStudent().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + openMaterial.getStudent().getId()));

        if (!SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals(student1.getUsername())){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<>(openMaterial, HttpStatus.OK);
    }

    @PostMapping("/students/{studentId}/openMaterials/{materialId}")
    public ResponseEntity<OpenMaterial> createOpenMaterial(@PathVariable(value = "studentId") Long studentId,
                                                           @PathVariable(value = "materialId") Long materialId,
                                             @RequestBody OpenMaterial openMaterialRequest) {
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


        OpenMaterial openMaterial = new OpenMaterial(student, material, openMaterialRequest.getDate_of_open());

        OpenMaterial _openMaterial = openMaterialRepository.save(openMaterial);

        return new ResponseEntity<>(_openMaterial, HttpStatus.CREATED);
    }

    @PutMapping("/openMaterials/{id}")
    public ResponseEntity<OpenMaterial> updateOpenMaterial(@PathVariable("id") long id, @RequestBody OpenMaterial openMaterialRequest) {
        OpenMaterial openMaterial = openMaterialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("OpenMaterialId " + id + "not found"));

        Student student1 = studentRepository.findById(openMaterial.getStudent().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + openMaterial.getStudent().getId()));

        if (!SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals(student1.getUsername())){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        openMaterial.setDate_of_open(openMaterialRequest.getDate_of_open());

        return new ResponseEntity<>(openMaterialRepository.save(openMaterial), HttpStatus.OK);
    }

    @DeleteMapping("/openMaterials/{id}")
    public ResponseEntity<HttpStatus> deleteOpenMaterial(@PathVariable("id") long id) {
        OpenMaterial openMaterial = openMaterialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("OpenMaterialId " + id + "not found"));

        Student student1 = studentRepository.findById(openMaterial.getStudent().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + openMaterial.getStudent().getId()));

        if (!SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals(student1.getUsername())){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        openMaterialRepository.deleteById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/students/{studentId}/openMaterials")
    public ResponseEntity<List<OpenMaterial>> deleteAllOpenMaterialsOfStudent(@PathVariable(value = "studentId") Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Not found Student with id = " + studentId);
        }
        Student student1 = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + studentId));

        if (!SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals(student1.getUsername())){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }


        openMaterialRepository.deleteByStudentId(studentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/materials/{materialId}/openMaterials")
    public ResponseEntity<List<OpenMaterial>> deleteAllOpenMaterialsOfMaterial(@PathVariable(value = "materialId") Long materialId) {
        if (!materialRepository.existsById(materialId)) {
            throw new ResourceNotFoundException("Not found Material with id = " + materialId);
        }

        openMaterialRepository.deleteByMaterialId(materialId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}


