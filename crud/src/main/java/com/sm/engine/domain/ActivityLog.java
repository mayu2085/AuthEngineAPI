package com.sm.engine.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * The activity log document definition.
 */
@Document
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLog extends IdentifiableDocument {

    /**
     * The operation type.
     */
    private OperationType operationType;

    /**
     * The document type under operation.
     */
    private String documentType;

    /**
     * The username of user who made the operation.
     */
    private String operatedBy;

    /**
     * The activity log description.
     */
    private String description;
}
