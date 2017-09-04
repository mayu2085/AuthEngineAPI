package com.sm.engine.service;

import com.sm.engine.domain.User;
import com.sm.engine.domain.support.UserSearchCriteria;
import com.sm.engine.repository.UserRepository;
import com.sm.engine.utils.Helper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.contains;
import static org.springframework.data.domain.ExampleMatcher.matching;

/**
 * The service provides operations to User document.
 */
@Service
@Transactional
public class UserService extends BaseService<User, UserSearchCriteria> {

    /**
     * The user repository.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Find user document by username.
     *
     * @param username the username
     * @return the match user document
     */
    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        Helper.checkNullOrEmpty(username, "username");
        return userRepository.findByUsername(username);
    }


    /**
     * Creates the search example instance.
     *
     * @param criteria the search criteria
     * @return the example instance
     */
    @Override
    protected Example<User> createSearchExample(UserSearchCriteria criteria) {
        User user = new User();
        user.setUsername(criteria.getUsername());
        user.setRole(criteria.getRole());
        user.setEnabled(criteria.getEnabled());

        return Example.of(user, matching().withMatcher("username", contains()));
    }
}
