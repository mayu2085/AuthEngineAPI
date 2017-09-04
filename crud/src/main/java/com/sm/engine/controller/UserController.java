package com.sm.engine.controller;

import com.sm.engine.domain.User;
import com.sm.engine.domain.support.UserSearchCriteria;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The controller of users end-points.
 */
@RestController
@RequestMapping("/users")
public class UserController extends BaseController<User, UserSearchCriteria> {
}
