package com.sm.engine.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.DBRef;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * Presents the rule name and information.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "name")
public class Rule {

    /**
     * The name, optional.
     */
    private String name;

    /**
     * The rule information.
     */
    @NotNull
    @Size(min = 1)
    @Valid
    private List<LdapAttributeNameValue> ruleInfo;

    /**
     * The associated header.
     */
    @NotNull
    @DBRef
    private Header header;
}
