package com.adictosaltrabajo.ai.model;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface FurnitureRepository {
    @Transactional
    void save(Furniture furniture);
    @Transactional(readOnly = true)
    long count();
    @Transactional(readOnly = true)
    List<Furniture> findBySimilarity(String content, Double topPrice, int maxResults);
}
