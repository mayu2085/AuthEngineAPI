package com.sm.engine.domain.support;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Presents the search criteria for searching Module.
 */
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ModuleSearchCriteria extends NameSearchCriteria {

    /**
     * The system id criterion.
     */
    private String systemId;
}
