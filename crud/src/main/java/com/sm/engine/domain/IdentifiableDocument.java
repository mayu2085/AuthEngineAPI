package com.sm.engine.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.Date;

/**
 * The base class for all documents that have ID.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public abstract class IdentifiableDocument {

    /**
     * The identifier.
     */
    @Id
    private String id;

    /**
     * The created date.
     */
    @CreatedDate
    private Date createdAt;

    /**
     * The last modified date.
     */
    @LastModifiedDate
    private Date lastModifiedAt;
}
