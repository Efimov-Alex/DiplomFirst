package ru.efimov.DiplomFirst.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.efimov.DiplomFirst.entity.CloseMaterial;
import ru.efimov.DiplomFirst.entity.OpenMaterial;
import ru.efimov.DiplomFirst.repository.CloseMaterialRepository;
import ru.efimov.DiplomFirst.repository.OpenMaterialRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@CrossOrigin(origins = "http://localhost:8083")
@RestController
@RequestMapping("/api")
public class CloseMaterialController {
    @Autowired
    CloseMaterialRepository closeMaterialRepository;

    @GetMapping("/closematerials")
    public ResponseEntity<List<CloseMaterial>> getAllCloseMaterials() {
        try {
            List<CloseMaterial> closeMaterials = new ArrayList<CloseMaterial>();

            closeMaterialRepository.findAll().forEach(closeMaterials::add);

            if (closeMaterials.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(closeMaterials, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/closematerials/{id}")
    public ResponseEntity<CloseMaterial> getCloseMaterialById(@PathVariable("id") long id) {
        Optional<CloseMaterial> closeMaterialData = closeMaterialRepository.findById(id);

        if (closeMaterialData.isPresent()) {
            return new ResponseEntity<>(closeMaterialData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/closematerials")
    public ResponseEntity<CloseMaterial> createCloseMaterial(@RequestBody CloseMaterial closeMaterial) {
        try {
            CloseMaterial _closeMaterial = closeMaterialRepository
                    .save(new CloseMaterial(closeMaterial.getStudent(), closeMaterial.getMaterial(), closeMaterial.getDate_of_close()));
            return new ResponseEntity<>(_closeMaterial, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/closematerials/{id}")
    public ResponseEntity<CloseMaterial> updateCloseMaterial(@PathVariable("id") long id, @RequestBody CloseMaterial closeMaterial) {
        Optional<CloseMaterial> closeMaterialData = closeMaterialRepository.findById(id);

        if (closeMaterialData.isPresent()) {
            CloseMaterial _closeMaterial = closeMaterialData.get();
            _closeMaterial.setStudent(closeMaterial.getStudent());
            _closeMaterial.setMaterial(closeMaterial.getMaterial());
            _closeMaterial.setDate_of_close(closeMaterial.getDate_of_close());

            return new ResponseEntity<>(closeMaterialRepository.save(_closeMaterial), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/closematerials/{id}")
    public ResponseEntity<HttpStatus> deleteCloseMaterial(@PathVariable("id") long id) {
        try {
            closeMaterialRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/closematerials")
    public ResponseEntity<HttpStatus> deleteAllCloseMaterials() {
        try {
            closeMaterialRepository.deleteAll();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }



}


