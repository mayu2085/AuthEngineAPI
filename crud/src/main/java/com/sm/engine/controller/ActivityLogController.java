package com.sm.engine.controller;

import com.sm.engine.domain.ActivityLog;
import com.sm.engine.domain.support.ActivityLogSearchCriteria;
import com.sm.engine.domain.support.SearchResult;
import com.sm.engine.service.ActivityLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Defines the activity-logs searching end-point. There is no add/update/delete end-points for
 * activity-logs.
 */
@RestController
@RequestMapping("/activity-logs")
public class ActivityLogController {

    /**
     * The service instance.
     */
    @Autowired
    private ActivityLogService activityLogService;

    /**
     * Search activity logs.
     *
     * @param criteria the search criteria
     * @param pageable the paging criteria
     * @return the search result
     */
    @GetMapping
    public SearchResult<ActivityLog> search(@ModelAttribute ActivityLogSearchCriteria criteria,
                                            Pageable pageable) {
        return activityLogService.search(criteria, pageable);
    }
}
