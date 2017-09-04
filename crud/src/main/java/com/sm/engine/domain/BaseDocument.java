package com.sm.engine.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;

import java.util.Date;

/**
 * The base class for all documents.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseDocument {

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
}
