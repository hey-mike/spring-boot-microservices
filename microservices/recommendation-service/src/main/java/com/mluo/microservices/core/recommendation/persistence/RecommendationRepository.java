package com.mluo.microservices.core.recommendation.persistence;


import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.List;

public interface RecommendationRepository extends ReactiveCrudRepository<RecommendationEntity, String> {
    List<RecommendationEntity> findByProductId(int productId);
}
