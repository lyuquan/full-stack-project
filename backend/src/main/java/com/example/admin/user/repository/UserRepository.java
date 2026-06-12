package com.example.admin.user.repository;

import com.example.admin.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * User database access layer.
 *
 * JpaRepository already provides common methods such as findAll, findById,
 * save and delete. We do not need to write SQL for basic CRUD now.
 */
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    /**
     * Search users by optional keyword and optional status.
     *
     * keyword can match username or nickname.
     * status can be enabled or disabled.
     */
    @Query("select u from UserEntity u " +
            "where (:keyword is null or :keyword = '' " +
            "or lower(u.username) like lower(concat('%', :keyword, '%')) " +
            "or lower(u.nickname) like lower(concat('%', :keyword, '%'))) " +
            "and (:status is null or :status = '' or u.status = :status)")
    List<UserEntity> searchUsers(@Param("keyword") String keyword, @Param("status") String status);
}
