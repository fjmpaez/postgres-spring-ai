package com.adictosaltrabajo.ai.adapter.database;

import com.adictosaltrabajo.ai.model.Furniture;
import com.adictosaltrabajo.ai.model.FurnitureRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@Profile("!vector-store")
public class JustJpaFurnitureRepository implements FurnitureRepository {

    private final JpaFurnitureDao jpaFurnitureDao;
    private final EmbeddingModel embeddingModel;

    @PersistenceContext
    private final EntityManager entityManager;

    public JustJpaFurnitureRepository(JpaFurnitureDao jpaFurnitureDao, EmbeddingModel embeddingModel, EntityManager entityManager) {
        this.jpaFurnitureDao = jpaFurnitureDao;
        this.embeddingModel = embeddingModel;
        this.entityManager = entityManager;
    }

    @Override
    public void save(Furniture furniture) {
        final Furniture furnitureEmbedded = furniture.toBuilder().id(UUID.randomUUID()).embedding(embeddingModel.embed(furniture.getContent())).build();
        jpaFurnitureDao.save(furnitureEmbedded);
    }

    @Override
    public long count() {
        return jpaFurnitureDao.count();
    }

    @Override
    public List<Furniture> findBySimilarity(String content, Double topPrice, int maxResults) {

        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM furniture");

        if (topPrice > 0) {
            queryBuilder.append(" WHERE price <= :topPrice");
        }

        queryBuilder.append(" ORDER BY embedding <=> :content ::vector");

        final Query query = entityManager.createNativeQuery(queryBuilder.toString(), Furniture.class).setMaxResults(maxResults);
        query.setParameter("content", embeddingModel.embed(content));

        if (topPrice > 0) {
            query.setParameter("topPrice", topPrice);
        }

        return query.getResultList();
    }
}
