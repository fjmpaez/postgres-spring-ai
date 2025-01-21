package com.adictosaltrabajo.ai;

import com.adictosaltrabajo.ai.model.Furniture;

import java.util.List;

public interface FurnitureRepository {
    void save(Furniture furniture);
    long count();
    List<Furniture> findBySimilarity(String content, Double topPrice, int maxResults);
}
