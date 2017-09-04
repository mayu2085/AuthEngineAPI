package com.sm.engine.controller;

import com.sm.engine.domain.Policy;
import com.sm.engine.domain.support.PolicySearchCriteria;
import com.sm.engine.service.PolicyService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * The controller of policies end-points.
 */
@RestController
@RequestMapping("/policies")
public class PolicyController extends BaseController<Policy, PolicySearchCriteria> {

    /**
     * Delete existing documents by ids.
     *
     * @param ids the document ids to delete
     */
    @DeleteMapping
    public void deleteByIds(@RequestParam(value = "ids") List<String> ids) {
        PolicyService policyService = (PolicyService) getService();
        policyService.deleteByIds(ids);
    }
}
