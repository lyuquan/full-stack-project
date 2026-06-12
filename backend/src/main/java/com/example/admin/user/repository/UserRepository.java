package com.example.admin.user.repository;

import com.example.admin.user.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * User database access layer.
 *
 * JpaRepository already provides common methods such as findAll, findById,
 * save and delete. Custom query methods are added here when list pages need
 * filters or pagination.
 */
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    /**
     * Check whether a username already exists.
     *
     * Spring Data JPA creates the SQL automatically from the method name.
     */
    boolean existsByUsername(String username);

    /**
     * Check whether a username is used by another user.
     *
     * This is used while editing: the current user can keep their own username,
     * but cannot change it to another user's username.
     */
    boolean existsByUsernameAndIdNot(String username, Long id);

    /**
     * Search users by optional keyword, role and status.
     *
     * Pageable tells JPA which page and how many records should be queried.
     */
    @Query("select u from UserEntity u " +
            "where (:keyword is null or :keyword = '' " +
            "or lower(u.username) like lower(concat('%', :keyword, '%')) " +
            "or lower(u.nickname) like lower(concat('%', :keyword, '%'))) " +
            "and (:role is null or :role = '' or u.role = :role) " +
            "and (:status is null or :status = '' or u.status = :status)")
    Page<UserEntity> searchUsers(
            @Param("keyword") String keyword,
            @Param("role") String role,
            @Param("status") String status,
            Pageable pageable
    );
}
