package com.sm.engine.domain.support;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Presents the search criteria for searching Policy.
 */
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class PolicySearchCriteria extends NameSearchCriteria {

    /**
     * The module id criterion.
     */
    private String moduleId;

    /**
     * The policy status.
     */
    private Boolean enabled;
}
