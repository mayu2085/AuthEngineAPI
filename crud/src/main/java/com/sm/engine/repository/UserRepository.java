package com.sm.engine.repository;

import com.sm.engine.domain.User;
import org.springframework.stereotype.Repository;

/**
 * The user repository.
 */
@Repository
public interface UserRepository extends BaseRepository<User> {
    /**
     * Find user document by username.
     *
     * @param username the username
     * @return the match user document
     */
    User findByUsername(String username);
}
