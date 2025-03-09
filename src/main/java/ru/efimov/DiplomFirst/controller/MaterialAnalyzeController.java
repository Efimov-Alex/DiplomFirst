package ru.efimov.DiplomFirst.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.efimov.DiplomFirst.entity.MaterialAnalyze;
import ru.efimov.DiplomFirst.entity.ProfileAnalyze;
import ru.efimov.DiplomFirst.repository.MaterialAnalyzeRepository;
import ru.efimov.DiplomFirst.repository.MaterialRepository;
import ru.efimov.DiplomFirst.repository.ProfileAnalyzeRepository;
import ru.efimov.DiplomFirst.repository.StudentRepository;

import java.util.List;

@CrossOrigin(origins = "http://localhost:8083")
@RestController
@RequestMapping("/api")
public class MaterialAnalyzeController {
    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private MaterialAnalyzeRepository materialAnalyzeRepository;





    @GetMapping("/materialAnalyze/{id}")
    public ResponseEntity<MaterialAnalyze> getmaterialAnalyzeByStudentId(@PathVariable(value = "id") Long id) {
        MaterialAnalyze materialAnalyze = materialAnalyzeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found materialAnalyze with id = " + id));

        return new ResponseEntity<>(materialAnalyze, HttpStatus.OK);
    }

    @PostMapping("/materials/{materialId}/materialAnalyzes")
    public ResponseEntity<MaterialAnalyze> createMaterialAnalyze(@PathVariable(value = "materialId") Long materialId,
                                                               @RequestBody MaterialAnalyze materialAnalyzeRequest) {
        MaterialAnalyze materialAnalyze = materialRepository.findById(materialId).map(material -> {
            materialAnalyzeRequest.setMaterial(material);
            return materialAnalyzeRepository.save(materialAnalyzeRequest);
        }).orElseThrow(() -> new ResourceNotFoundException("Not found Material with id = " + materialId));

        return new ResponseEntity<>(materialAnalyze, HttpStatus.CREATED);
    }

    @PutMapping("/materialAnalyzes/{id}")
    public ResponseEntity<MaterialAnalyze> updateMaterialAnalyze(@PathVariable("id") long id, @RequestBody MaterialAnalyze materialAnalyzeRequest) {
        MaterialAnalyze materialAnalyze = materialAnalyzeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MaterialAnalyzeId " + id + "not found"));

        materialAnalyze.setDescripation(materialAnalyzeRequest.getDescripation());
        materialAnalyze.setCharacteristic(materialAnalyzeRequest.getCharacteristic());
        materialAnalyze.setValue(materialAnalyzeRequest.getValue());

        return new ResponseEntity<>(materialAnalyzeRepository.save(materialAnalyze), HttpStatus.OK);
    }

    @DeleteMapping("/materialAnalyzes/{id}")
    public ResponseEntity<HttpStatus> deleteMaterislAnalyze(@PathVariable("id") long id) {
        materialAnalyzeRepository.deleteById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/materials/{materialId}/materialAnalyzes")
    public ResponseEntity<List<MaterialAnalyze>> deleteAllmaterialAnalyzesOfMaterial(@PathVariable(value = "materialId") Long materialId) {
        if (!materialRepository.existsById(materialId)) {
            throw new ResourceNotFoundException("Not found Material with id = " + materialId);
        }

        materialAnalyzeRepository.deleteByMaterialId(materialId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}


