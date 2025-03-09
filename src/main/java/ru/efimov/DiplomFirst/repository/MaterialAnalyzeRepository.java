package ru.efimov.DiplomFirst.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.efimov.DiplomFirst.entity.CloseMaterial;
import ru.efimov.DiplomFirst.entity.MaterialAnalyze;

import javax.transaction.Transactional;
import java.util.List;

public interface MaterialAnalyzeRepository extends JpaRepository<MaterialAnalyze, Long> {

    List<MaterialAnalyze> findByMaterialId(Long materialId);

    @Transactional
    void deleteByMaterialId(long materialId);

}