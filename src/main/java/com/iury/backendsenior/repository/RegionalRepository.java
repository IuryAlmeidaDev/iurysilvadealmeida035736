package com.iury.backendsenior.repository;

import com.iury.backendsenior.model.Regional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RegionalRepository extends JpaRepository<Regional, Long> {

    List<Regional> findByAtivoTrue();

    List<Regional> findByAtivo(Boolean ativo);

    Optional<Regional> findFirstByIdAndAtivoTrue(Integer id);
}
