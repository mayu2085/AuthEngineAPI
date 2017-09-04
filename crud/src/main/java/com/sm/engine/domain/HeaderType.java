package com.sm.engine.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The static/dynamic header types
 */
public enum HeaderType {
    /**
     * The static header type.
     */
    @JsonProperty("Static")
    STATIC,
    /**
     * The dynamic header type.
     */
    @JsonProperty("Dynamic")
    DYNAMIC;
}
