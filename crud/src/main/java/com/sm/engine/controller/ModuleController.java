package com.sm.engine.controller;

import com.sm.engine.domain.Module;
import com.sm.engine.domain.support.ModuleSearchCriteria;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The controller of modules end-points.
 */
@RestController
@RequestMapping("/modules")
public class ModuleController extends BaseController<Module, ModuleSearchCriteria> {
}
