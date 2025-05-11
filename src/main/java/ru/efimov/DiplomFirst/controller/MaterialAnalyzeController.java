package ru.efimov.DiplomFirst.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.efimov.DiplomFirst.entity.*;
import ru.efimov.DiplomFirst.repository.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@CrossOrigin(origins = "http://localhost:8083")
@RestController
@RequestMapping("/api")
public class MaterialAnalyzeController {
    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private MaterialAnalyzeRepository materialAnalyzeRepository;


    @Autowired
    private OpenMaterialRepository openMaterialRepository;

    @Autowired
    private CloseMaterialRepository closeMaterialRepository;

    private static final Logger logger = LogManager.getLogger(MaterialAnalyzeController.class);




    @GetMapping("/materials/{materialId}/metrics")
    public ResponseEntity<MaterialAnalyze> getMaterialMetrics(@PathVariable(value = "materialId") Long materialId
    ) {
        List<MaterialAnalyze> materialAnalyzes = materialAnalyzeRepository.findByMaterialId(materialId);

        if (materialAnalyzes.size() != 1){
            logger.error("Not found materialAnalyze with materialId = " + materialId);
            throw new ResourceNotFoundException("Not found materialAnalyze with materialId = " + materialId);
        }
        MaterialAnalyze oldMaterialAnalyze = materialAnalyzes.get(0);

        List<OpenMaterial> openMaterials = openMaterialRepository.findByMaterialId(materialId);
        List<CloseMaterial> closeMaterials = closeMaterialRepository.findByMaterialId(materialId);

        Collections.sort(openMaterials, new Comparator<OpenMaterial>() {
            @Override
            public int compare(OpenMaterial a1, OpenMaterial a2) {
                return a1.getDate_of_open().compareTo(a2.getDate_of_open());
            }
        });

        Collections.sort(closeMaterials, new Comparator<CloseMaterial>() {
            @Override
            public int compare(CloseMaterial a1, CloseMaterial a2) {
                return a1.getDate_of_close().compareTo(a2.getDate_of_close());
            }
        });

        HashMap<Long, List<OpenMaterial>> map = new HashMap<>();

        for (OpenMaterial o1 : openMaterials){
            if (!map.containsKey(o1.getStudent().getId())){
                List<OpenMaterial> list1 = new ArrayList<>();
                list1.add(o1);
                map.put(o1.getStudent().getId(), list1);
            }
            else{
                List<OpenMaterial> list1 = map.get(o1.getStudent().getId());
                list1.add(0, o1);
                map.put(o1.getStudent().getId(), list1);
            }
        }

        double totalTimeSum = 0;
        long timeCount = 0;

        for (CloseMaterial o1 : closeMaterials){
            if (!map.containsKey(o1.getStudent().getId())){
                continue;
            }
            else{
                List<OpenMaterial> list1 = map.get(o1.getStudent().getId());
                if (list1.size() == 0){
                    logger.error("Not found OpenMaterial");
                    throw new ResourceNotFoundException("Not found OpenMaterial");
                }
                OpenMaterial o2 = list1.remove(list1.size()-1);
                map.put(o1.getStudent().getId(), list1);

                if (o2.getDate_of_open().compareTo(o1.getDate_of_close()) > 0){
                    continue;
                }
                Optional<Material> curMaterial = materialRepository.findById(o2.getMaterial().getId());
                long minutes = ChronoUnit.MINUTES.between(o2.getDate_of_open(), o1.getDate_of_close());

                totalTimeSum += (float) minutes / curMaterial.get().getTime_for_learning();
                timeCount += 1;
            }
        }
        MaterialAnalyze newMaterialAnalyze = new MaterialAnalyze();
        newMaterialAnalyze.setId(oldMaterialAnalyze.getId());
        newMaterialAnalyze.setMaterial(oldMaterialAnalyze.getMaterial());
        newMaterialAnalyze.setMean_time(oldMaterialAnalyze.getMean_time());

        if (timeCount == 0){
            logger.error("Not found OpenMaterial");
            throw new ResourceNotFoundException("Not found OpenMaterial");
        }

        float averageTime = (float) totalTimeSum / timeCount;
        newMaterialAnalyze.setMean_time(averageTime);


        materialAnalyzeRepository.save(newMaterialAnalyze);



        logger.info("Вывод метрик материалов ");
        return new ResponseEntity<>(newMaterialAnalyze, HttpStatus.CREATED);
    }





    @GetMapping("/materialAnalyze/{id}")
    public ResponseEntity<MaterialAnalyze> getmaterialAnalyzeByStudentId(@PathVariable(value = "id") Long id) {
        MaterialAnalyze materialAnalyze = materialAnalyzeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found materialAnalyze with id = " + id));

        logger.info("Получение MaterialAnalyze по id " + id);
        return new ResponseEntity<>(materialAnalyze, HttpStatus.OK);
    }

    @PostMapping("/materials/{materialId}/materialAnalyzes")
    public ResponseEntity<MaterialAnalyze> createMaterialAnalyze(@PathVariable(value = "materialId") Long materialId,
                                                               @RequestBody MaterialAnalyze materialAnalyzeRequest) {
        MaterialAnalyze materialAnalyze = materialRepository.findById(materialId).map(material -> {
            materialAnalyzeRequest.setMaterial(material);
            return materialAnalyzeRepository.save(materialAnalyzeRequest);
        }).orElseThrow(() -> new ResourceNotFoundException("Not found Material with id = " + materialId));

        logger.info("Создание MaterialAnalyze по materialId " + materialId);
        return new ResponseEntity<>(materialAnalyze, HttpStatus.CREATED);
    }

    @PutMapping("/materialAnalyzes/{id}")
    public ResponseEntity<MaterialAnalyze> updateMaterialAnalyze(@PathVariable("id") long id, @RequestBody MaterialAnalyze materialAnalyzeRequest) {
        MaterialAnalyze materialAnalyze = materialAnalyzeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MaterialAnalyzeId " + id + "not found"));

        materialAnalyze.setMean_time(materialAnalyzeRequest.getMean_time());

        logger.info("Обновление MaterialAnalyze по id " + id);
        return new ResponseEntity<>(materialAnalyzeRepository.save(materialAnalyze), HttpStatus.OK);
    }

    @DeleteMapping("/materialAnalyzes/{id}")
    public ResponseEntity<HttpStatus> deleteMaterislAnalyze(@PathVariable("id") long id) {
        materialAnalyzeRepository.deleteById(id);

        logger.info("Удаление MaterialAnalyze по id " + id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/materials/{materialId}/materialAnalyzes")
    public ResponseEntity<List<MaterialAnalyze>> deleteAllmaterialAnalyzesOfMaterial(@PathVariable(value = "materialId") Long materialId) {
        if (!materialRepository.existsById(materialId)) {
            logger.error("Not found Material with id = " + materialId);
            throw new ResourceNotFoundException("Not found Material with id = " + materialId);
        }

        materialAnalyzeRepository.deleteByMaterialId(materialId);
        logger.info("Удаление всех MaterialAnalyze по materialId " + materialId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}


