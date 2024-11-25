package ru.efimov.DiplomFirst.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.efimov.DiplomFirst.entity.Material;
import ru.efimov.DiplomFirst.repository.MaterialRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:8083")
@RestController
@RequestMapping("/api")
public class MaterialController {
    @Autowired
    MaterialRepository materialRepository;

    @GetMapping("/materials")
    public ResponseEntity<List<Material>> getAllMaterials(@RequestParam(required = false) String title) {
        try {
            List<Material> materials = new ArrayList<Material>();

            materialRepository.findByTitleContaining(title).forEach(materials::add);

            if (materials.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(materials, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/materials/{id}")
    public ResponseEntity<Material> getMaterialById(@PathVariable("id") long id) {
        Optional<Material> materialData = materialRepository.findById(id);

        if (materialData.isPresent()) {
            return new ResponseEntity<>(materialData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/materials")
    public ResponseEntity<Material> createMaterial(@RequestBody Material material) {
        try {
            Material _material = materialRepository
                    .save(new Material(material.getTitle(), material.getDescription()));
            return new ResponseEntity<>(_material, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/materials/{id}")
    public ResponseEntity<Material> updateMaterial(@PathVariable("id") long id, @RequestBody Material material) {
        Optional<Material> materialData = materialRepository.findById(id);

        if (materialData.isPresent()) {
            Material _material = materialData.get();
            _material.setTitle(material.getTitle());
            _material.setDescription(material.getDescription());

            return new ResponseEntity<>(materialRepository.save(_material), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/materials/{id}")
    public ResponseEntity<HttpStatus> deleteMaterial(@PathVariable("id") long id) {
        try {
            materialRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/materials")
    public ResponseEntity<HttpStatus> deleteAllMaterials() {
        try {
            materialRepository.deleteAll();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }



}
