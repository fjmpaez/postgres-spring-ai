package com.adictosaltrabajo.ai.adapter.database;

import com.adictosaltrabajo.ai.model.Furniture;
import com.adictosaltrabajo.ai.model.FurnitureRepository;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@Profile("vector-store")
public class JpaSpringAIFurnitureRepository implements FurnitureRepository {

    private final JpaFurnitureDao jpaFurnitureDao;
    private final VectorStore furnitureVectorStore;

    public JpaSpringAIFurnitureRepository(JpaFurnitureDao jpaFurnitureDao, VectorStore furnitureVectorStore) {
        this.jpaFurnitureDao = jpaFurnitureDao;
        this.furnitureVectorStore = furnitureVectorStore;
    }

    @Override
    public void save(Furniture furniture) {
        final Document document = Document.builder()
                .id(furniture.getId().toString())
                .text(furniture.getContent())
                .metadata("price", furniture.getPrice())
                .metadata("width", furniture.getWidth())
                .metadata("height", furniture.getHeight())
                .metadata("depth", furniture.getDepth())
                .build();
        furnitureVectorStore.add(List.of(document));
        jpaFurnitureDao.save(furniture);
    }

    @Override
    public long count() {
        return jpaFurnitureDao.count();
    }

    @Override
    public List<Furniture> findBySimilarity(String content, Double topPrice, int maxResults) {

        var searchRequest = SearchRequest.builder().query(content).topK(maxResults);

        if (topPrice > 0.0) {
            searchRequest.filterExpression("price <= " + topPrice);
        }

        List<UUID> uuids = furnitureVectorStore.similaritySearch(searchRequest.build()).stream().map(document -> UUID.fromString(document.getId())).toList();

        return jpaFurnitureDao.findAllById(uuids);
    }
}
