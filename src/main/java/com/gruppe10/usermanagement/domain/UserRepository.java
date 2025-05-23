//ChristianMarkow
package com.gruppe10.usermanagement.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    // If you don't need a total row count, Slice is better than Page.
    Slice<User> findAllBy(Pageable pageable);

    Optional<User> findByEmail(String email);

}