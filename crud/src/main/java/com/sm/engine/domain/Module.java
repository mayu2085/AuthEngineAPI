package com.sm.engine.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * The module document definition.
 */
@Document
@CompoundIndex(name = "name_system", unique = true, def = "{ 'name': 1, 'system': 1 }")
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Module extends NamedDocument {

    /**
     * The description.
     */
    private String description;

    /**
     * The system to which the module belongs.
     */
    @NotNull
    @DBRef
    private System system;
}
