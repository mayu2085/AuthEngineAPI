package com.sm.engine.service;

import com.sm.engine.domain.ActivityLog;
import com.sm.engine.domain.support.ActivityLogSearchCriteria;
import com.sm.engine.domain.support.SearchResult;
import com.sm.engine.repository.ActivityLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static org.springframework.data.domain.ExampleMatcher.matching;

/**
 * The service provides operations on Activity Log document.
 */
@Service
public class ActivityLogService {

    /**
     * The repository instance.
     */
    @Autowired
    private ActivityLogRepository activityLogRepository;

    /**
     * Searches documents with search example and pageable request.
     *
     * @param criteria the search criteria
     * @param pageable the pageable request
     * @return the results with pagination
     */
    public SearchResult<ActivityLog> search(ActivityLogSearchCriteria criteria, Pageable pageable) {
        ActivityLog activityLog = new ActivityLog();
        activityLog.setDocumentType(criteria.getDocumentType());
        activityLog.setDescription(criteria.getDescription());
        activityLog.setOperatedBy(criteria.getOperatedBy());
        activityLog.setOperationType(criteria.getOperationType());

        Example<ActivityLog> example =
                Example.of(activityLog, matching().withStringMatcher(StringMatcher.CONTAINING));
        return new SearchResult<>(activityLogRepository.findAll(example, pageable));
    }
}
