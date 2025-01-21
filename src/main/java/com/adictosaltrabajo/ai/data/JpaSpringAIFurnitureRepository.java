package com.adictosaltrabajo.ai.data;

import com.adictosaltrabajo.ai.FurnitureRepository;
import com.adictosaltrabajo.ai.model.Furniture;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
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
                .withId(furniture.getId().toString())
                .withContent(furniture.getContent())
                .withMetadata("price", furniture.getPrice())
                .withMetadata("width", furniture.getWidth())
                .withMetadata("height", furniture.getHeight())
                .withMetadata("depth", furniture.getDepth())
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

        final SearchRequest searchRequest = SearchRequest.query(content).withTopK(maxResults);

        if(topPrice > 0.0) {
            searchRequest.withFilterExpression("price <= " + topPrice);
        }

        List<UUID> uuids = furnitureVectorStore.similaritySearch(searchRequest).stream().map(document -> UUID.fromString(document.getId())).toList();

        return jpaFurnitureDao.findAllById(uuids);
    }
}
