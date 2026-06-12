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
     * Search users by optional keyword and optional status.
     *
     * Pageable tells JPA which page and how many records should be queried.
     */
    @Query("select u from UserEntity u " +
            "where (:keyword is null or :keyword = '' " +
            "or lower(u.username) like lower(concat('%', :keyword, '%')) " +
            "or lower(u.nickname) like lower(concat('%', :keyword, '%'))) " +
            "and (:status is null or :status = '' or u.status = :status)")
    Page<UserEntity> searchUsers(
            @Param("keyword") String keyword,
            @Param("status") String status,
            Pageable pageable
    );
}
