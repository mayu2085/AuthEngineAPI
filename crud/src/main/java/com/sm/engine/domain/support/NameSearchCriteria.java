package com.sm.engine.domain.support;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Presents the search criteria that have a name criterion.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class NameSearchCriteria {

    /**
     * The name criterion.
     */
    private String name;
}
