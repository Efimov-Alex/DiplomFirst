package ru.efimov.DiplomFirst.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.efimov.DiplomFirst.entity.Enter;
import ru.efimov.DiplomFirst.entity.OpenMaterial;

import javax.transaction.Transactional;
import java.util.List;

public interface OpenMaterialRepository extends JpaRepository<OpenMaterial, Long> {
    List<OpenMaterial> findByStudentId(Long studentId);

    @Transactional
    void deleteByStudentId(long studentId);

    List<OpenMaterial> findByMaterialId(Long materialId);

    @Transactional
    void deleteByMaterialId(long materialId);


}
