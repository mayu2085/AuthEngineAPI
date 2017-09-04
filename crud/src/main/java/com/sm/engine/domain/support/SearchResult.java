package com.sm.engine.domain.support;

import com.sm.engine.domain.IdentifiableDocument;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Presents the search result.
 *
 * @param <T> the document of the search
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
public class SearchResult<T extends IdentifiableDocument> {

    /**
     * The total record found.
     */
    private long total;

    /**
     * The records found with pagination.
     */
    private List<T> records;

    /**
     * Create a new instance of Spring <code>Page</code>.
     *
     * @param springPage the Spring Page instance
     */
    public SearchResult(Page<T> springPage) {
        this.total = springPage.getTotalElements();
        this.records = springPage.getContent();
    }
}
