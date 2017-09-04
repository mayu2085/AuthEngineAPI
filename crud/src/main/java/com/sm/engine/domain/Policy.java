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
import java.util.List;

/**
 * The policy document definition.
 */
@Document
@CompoundIndex(name = "name_module", unique = true, def = "{ 'name': 1, 'module': 1 }")
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Policy extends NamedDocument {

    /**
     * The role associated to the user.
     */
    private String description;

    /**
     * The policy status.
     */
    @NotNull
    private Boolean enabled;

    /**
     * The rules.
     */
    @Valid
    private List<Rule> rules;

    /**
     * The module to which the policy belongs.
     */
    @NotNull
    @DBRef
    private Module module;
}
