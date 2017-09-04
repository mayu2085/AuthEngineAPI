package com.sm.engine.service;

import com.sm.engine.domain.ActivityLog;
import com.sm.engine.domain.IdentifiableDocument;
import com.sm.engine.domain.OperationType;
import com.sm.engine.domain.support.SearchResult;
import com.sm.engine.exception.DocumentNotFoundException;
import com.sm.engine.repository.ActivityLogRepository;
import com.sm.engine.repository.BaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Set;
import java.util.Iterator;
import java.lang.reflect.Field;
import javax.validation.Validator;
import javax.validation.Path.Node;
import javax.validation.ConstraintViolation;

/**
 * Base service provides CRUD operations.
 *
 * @param <T> the document type
 * @param <S> the search criteria type
 */
@Transactional
public abstract class BaseService<T extends IdentifiableDocument, S> {


    /**
     * The repository instance.
     */
    @Autowired
    private BaseRepository<T> repository;

    /**
     * The activity log repository instance.
     */
    @Autowired
    private ActivityLogRepository activityLogRepository;

    @Autowired
    Validator validator;

    /**
     * Creates a new document.
     *
     * @param document the document to create
     * @return the created document
     */
    public T create(T document) {
        validateAndPopulateReferences(document);

        T createdDocument = repository.insert(document);

        createActivityLog(OperationType.Create, null, createdDocument);

        return createdDocument;
    }

    /**
     * Updates an existing document.
     *
     * @param id       the document id
     * @param document the document to update
     * @return the updated document
     */
    public T update(String id, T document) {
        T existingDocument = ensureExist(id);
        validateAndPopulateReferences(document);

        document.setId(id);
        document.setCreatedAt(existingDocument.getCreatedAt());
        T updatedDocument = repository.save(document);

        createActivityLog(OperationType.Change, existingDocument, updatedDocument);

        return updatedDocument;
    }

    /**
     * Gets a document by id.
     *
     * @param id the document id
     * @return the document
     */
    @Transactional(readOnly = true)
    public T get(String id) {
        return ensureExist(id);
    }

    /**
     * Deletes a document by id.
     *
     * @param id the document id
     */
    public void delete(String id) {
        T existingDocument = ensureExist(id);

        repository.delete(id);

        createActivityLog(OperationType.Delete, existingDocument, null);
    }

    /**
     * Searches documents with search example and pageable request.
     *
     * @param criteria the search criteria
     * @param pageable the pageable request
     * @return the results with pagination
     */
    @Transactional(readOnly = true)
    public SearchResult<T> search(S criteria, Pageable pageable) {
        Example<T> example = createSearchExample(criteria);
        return new SearchResult<T>(repository.findAll(example, pageable));
    }

    /**
     * Validate all documents referenced by the specified document. Concrete classes should overwrite
     * if necessary.
     * It will also create or update referenced documents if necessary.
     *
     * @param document the document to validate/populate
     * @throws IllegalArgumentException if there's any error
     */
    protected void validateAndPopulateReferences(T document) {
    }


    /**
     * Validates that the list doesn't include null or duplicated items.
     *
     * @param list     the list to validate
     * @param listName the list name to build the exception if failed to validate
     * @throws IllegalArgumentException when having null or duplicated items
     */
    protected static <T> void validateList(List<T> list, String listName) {
        if (list != null) {
            list.forEach(item -> {
                if (item == null) {
                    throw new IllegalArgumentException(
                            String.format("%s must not include null items", listName));
                }
            });

            long uniqueCount = list.stream().distinct().count();
            if (list.size() > uniqueCount) {
                throw new IllegalArgumentException(
                        String.format("%s must not include duplicated items", listName));
            }
        }
    }

    /**
     * Creates the search example instance.
     *
     * @param criteria the search criteria
     * @return the example instance
     */
    protected abstract Example<T> createSearchExample(S criteria);

    /**
     * Create or Update document in database.
     *
     * @param doc the document to check
     * @return the document if found
     * @throws IllegalArgumentException if not exist
     */
    protected T createOrUpdateDocument(T doc) {
        String id = doc.getId();
        if (id == null) {
            return create(doc);
        } else if (isOnlyId(doc)){
            return get(id);
        } else {
            validateObject(doc);
            return update(id, doc);
        }
    }

    /**
     * Validate object properties.
     *
     * @param doc the document to check
     * @throws IllegalArgumentException if validation fails
     */
    private void validateObject(T doc){
        Set<ConstraintViolation<T>> violations = validator.validate(doc);
        if (violations.size()>0){
            String message="";
            for (ConstraintViolation cv : violations){
                String path = "";
                Iterator<Node> nodeIter = cv.getPropertyPath().iterator();
                while(nodeIter.hasNext()){
                    if (path.length()>0) {
                        path +=".";
                    }
                    path+=nodeIter.next().getName();
                }
                if (message.length()>0){
                    message+=", ";
                }
                message+=path+" "+cv.getMessage();
            }
            throw new IllegalArgumentException(message);
        }
    }

    private boolean isOnlyId(T obj){
        try{
            for (Field f : obj.getClass().getDeclaredFields()) {
                f.setAccessible(true);
                if (f.get(obj) != null && !f.getName().equals("id")) {
                    return false;
                }
            }
            return true;
        }catch(Exception ex){
            return false;
        }
    }

    /**
     * Ensures that a document exists with the provided id.
     *
     * @param id the id
     * @return the document
     * @throws DocumentNotFoundException if there is no document with that id
     */
    private T ensureExist(String id) {
        T existing = repository.findOne(id);

        if (existing == null) {
            throw new DocumentNotFoundException(id);
        }

        return existing;
    }

    /**
     * Creates a activity log.
     *
     * @param operationType  the operation type
     * @param documentBefore the document before saving
     * @param documentAfter  the document after saving
     */
    private void createActivityLog(OperationType operationType, T documentBefore, T documentAfter) {
        final String documentType =
                ((documentBefore != null) ? documentBefore : documentAfter).getClass().getSimpleName();
        final String username = SecurityContextHolder.getContext().getAuthentication().getName();
        final String description =
                String.format("BEFORE: %s\nAFTER: %s", documentBefore, documentAfter);

        ActivityLog activityLog = new ActivityLog(operationType, documentType, username, description);

        activityLogRepository.insert(activityLog);
    }
}
