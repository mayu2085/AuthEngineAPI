package com.sm.engine.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;

/**
 * The user document definition.
 */
@Document
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class User extends IdentifiableDocument {

    /**
     * The username.
     */
    @NotNull
    @Indexed(unique = true)
    private String username;

    /**
     * The role associated to the user.
     */
    @NotNull
    private Role role;

    /**
     * The user status.
     */
    @NotNull
    private Boolean enabled;
}
