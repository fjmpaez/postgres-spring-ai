package com.adictosaltrabajo.ai.adapter;

import com.adictosaltrabajo.ai.model.Furniture;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

@Repository
public interface JpaFurnitureDao extends JpaRepository<Furniture, UUID> {
}
