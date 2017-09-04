package com.sm.engine.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;

/**
 * The header document definition.
 */
@Document
@CompoundIndex(name = "headerName", unique = true, def = "{ 'headerName': 1 }")
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "headerName", callSuper = true)
public class Header extends IdentifiableDocument {
    /**
     * The header type.
     */
    private HeaderType type;

    /**
     * The header name.
     */
    @NotNull
    private String headerName;

    /**
     * The role associated to the user.
     */
    private String description;

    /**
     * The header value.
     * String that will be True/False if type is Static, and will be the name of user attribute if type is Dynamic.
     */
    @NotNull
    private String value;
}
