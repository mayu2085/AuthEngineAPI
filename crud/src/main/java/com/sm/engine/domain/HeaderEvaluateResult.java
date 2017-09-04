package com.sm.engine.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * Presents the header evaluate result.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class HeaderEvaluateResult {
    /**
     * The header name.
     */
    @NotNull
    private String name;

    /**
     * The header value.
     */
    @NotNull
    private String value;
}
