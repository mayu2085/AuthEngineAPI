package com.sm.engine.domain.support;

import com.sm.engine.domain.OperationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Presents the search criteria for searching ActivityLog.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLogSearchCriteria {

    /**
     * The operation type criterion.
     */
    private OperationType operationType;

    /**
     * The document type criterion.
     */
    private String documentType;

    /**
     * The operated by criterion.
     */
    private String operatedBy;

    /**
     * The description criterion.
     */
    private String description;
}
