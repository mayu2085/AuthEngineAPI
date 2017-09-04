package com.sm.engine.repository;

import com.sm.engine.domain.IdentifiableDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * The base class for all CRUD repositories.
 *
 * @param <T> the document type
 */
@NoRepositoryBean
public interface BaseRepository<T extends IdentifiableDocument> extends MongoRepository<T, String> {
}
