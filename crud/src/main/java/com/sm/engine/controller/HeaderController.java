package com.sm.engine.controller;

import com.sm.engine.domain.Header;
import com.sm.engine.domain.HeaderEvaluateResult;
import com.sm.engine.domain.support.NameSearchCriteria;
import com.sm.engine.service.HeaderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * The controller of headers end-points.
 */
@RestController
@RequestMapping("/headers")
public class HeaderController extends BaseController<Header, NameSearchCriteria> {

    /**
     * Gets the Headers evaluate result generated for the specified username.
     *
     * @param username the username
     * @param moduleId the id of module
     * @return the Headers evaluate result
     */
    @GetMapping("/evaluate/{username}")
    public List<HeaderEvaluateResult> evaluate(@PathVariable("username") String username,
                                               @RequestParam(value = "moduleId", required = false) String moduleId) {
        return ((HeaderService) getService()).evaluate(username, moduleId);
    }
}
