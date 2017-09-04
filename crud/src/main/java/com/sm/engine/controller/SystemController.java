package com.sm.engine.controller;

import com.sm.engine.domain.System;
import com.sm.engine.domain.support.NameSearchCriteria;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The controller of systems end-points.
 */
@RestController
@RequestMapping("/systems")
public class SystemController extends BaseController<System, NameSearchCriteria> {
}
