package com.sm.engine.domain.support;

import com.sm.engine.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * Presents the search criteria for searching User.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchCriteria {

    /**
     * The username.
     */
    private String username;

    /**
     * The role.
     */
    private Role role;

    /**
     * The user status.
     */
    @NotNull
    private Boolean enabled;
}
