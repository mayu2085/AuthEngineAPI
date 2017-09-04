package com.sm.engine;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.sm.engine.domain.IdentifiableDocument;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

import java.util.Date;

/**
 * The test application.
 */
@Configuration
@ComponentScan(basePackages = {"com.sm.engine"})
@PropertySource({"classpath:application-test.properties"})
@EnableMongoAuditing
public class TestApplication extends Application {

    /**
     * Custom mixin to ignore last modified at
     */
    abstract class MixIn {
        /**
         * Ignore last modified at.
         */
        @JsonIgnore
        Date lastModifiedAt;
    }

    /**
     * Configure the object mapper.
     *
     * @return the object mapper
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.addMixIn(IdentifiableDocument.class, MixIn.class);
        return objectMapper;
    }
}
