package com.sm.engine.controller;

import com.sm.engine.domain.IdentifiableDocument;
import com.sm.engine.domain.support.SearchResult;
import com.sm.engine.service.BaseService;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * The base CRUD controller.
 *
 * @param <T> the document type
 * @param <S> the search criteria type
 */
public abstract class BaseController<T extends IdentifiableDocument, S> {

    /**
     * The service instance with CRUD operations.
     */
    @Autowired
    @Getter(value = AccessLevel.PROTECTED)
    private BaseService<T, S> service;

    /**
     * Create a new document.
     *
     * @param document the document to create
     * @return the created document
     */
    @PostMapping
    public T create(@Valid @RequestBody T document) {
        return service.create(document);
    }

    /**
     * Search documents.
     *
     * @param criteria the search criteria
     * @param pageable the paging criteria
     * @return the search result
     */
    @GetMapping
    public SearchResult<T> search(@ModelAttribute S criteria, @Valid Pageable pageable) {
        return service.search(criteria, pageable);
    }

    /**
     * Get an existing document by id.
     *
     * @param id the document id to get
     * @return the document
     */
    @GetMapping("/{id}")
    public T get(@PathVariable("id") String id) {
        return service.get(id);
    }

    /**
     * Update an existing document.
     *
     * @param id       the document id to update
     * @param document the document to update
     * @return the updated document
     */
    @PutMapping("/{id}")
    public T update(@PathVariable("id") String id, @Valid @RequestBody T document) {
        return service.update(id, document);
    }

    /**
     * Delete an existing document.
     *
     * @param id the document id to delete
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id) {
        service.delete(id);
    }
}
