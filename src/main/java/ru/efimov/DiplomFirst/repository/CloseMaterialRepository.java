package ru.efimov.DiplomFirst.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.efimov.DiplomFirst.entity.CloseMaterial;
import ru.efimov.DiplomFirst.entity.OpenMaterial;

public interface CloseMaterialRepository extends JpaRepository<CloseMaterial, Long> {


}