package ru.efimov.DiplomFirst.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.efimov.DiplomFirst.entity.CloseMaterial;
import ru.efimov.DiplomFirst.entity.OpenMaterial;

import javax.transaction.Transactional;
import java.util.List;

public interface CloseMaterialRepository extends JpaRepository<CloseMaterial, Long> {
    List<CloseMaterial> findByStudentId(Long studentId);

    @Transactional
    void deleteByStudentId(long studentId);

    List<CloseMaterial> findByMaterialId(Long materialId);

    @Transactional
    void deleteByMaterialId(long materialId);

}