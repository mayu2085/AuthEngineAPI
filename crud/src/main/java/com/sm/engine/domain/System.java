package com.sm.engine.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * The system document definition.
 */
@Document
@CompoundIndex(name = "name", unique = true, def = "{'name' : 1 }")
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class System extends NamedDocument {

    /**
     * The description.
     */
    private String description;
}
