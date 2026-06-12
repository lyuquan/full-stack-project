package com.example.admin.user.service;

import com.example.admin.common.PageResult;
import com.example.admin.user.dto.CreateUserDTO;
import com.example.admin.user.dto.UpdateUserDTO;
import com.example.admin.user.entity.UserEntity;
import com.example.admin.user.repository.UserRepository;
import com.example.admin.user.vo.UserVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * User business service.
 *
 * Service handles business logic and calls Repository to read/write database data.
 */
@Service
public class UserService {

    /**
     * Repository is the database access object injected by Spring.
     */
    private final UserRepository userRepository;

    /**
     * Constructor injection makes required dependencies clear.
     */
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Query users from the database with filters and pagination.
     *
     * The frontend page starts from 1, but Spring Data JPA page index starts from 0.
     */
    public PageResult<UserVO> listUsers(String keyword, String status, Integer page, Integer size) {
        int safePage = page == null || page < 1 ? 1 : page;
        int safeSize = size == null || size < 1 ? 5 : size;

        Pageable pageable = PageRequest.of(
                safePage - 1,
                safeSize,
                Sort.by(Sort.Direction.ASC, "id")
        );

        Page<UserEntity> entityPage = userRepository.searchUsers(keyword, status, pageable);
        List<UserVO> users = new ArrayList<UserVO>();

        for (UserEntity entity : entityPage.getContent()) {
            users.add(toVO(entity));
        }

        return new PageResult<UserVO>(
                users,
                entityPage.getTotalElements(),
                safePage,
                safeSize
        );
    }

    /**
     * Query one user by ID.
     *
     * findById returns Optional because the user may not exist in the database.
     */
    public UserVO getUserById(Long id) {
        Optional<UserEntity> optionalUser = userRepository.findById(id);

        if (!optionalUser.isPresent()) {
            return null;
        }

        return toVO(optionalUser.get());
    }

    /**
     * Create a new user and save it to the database.
     */
    public UserVO createUser(CreateUserDTO createUserDTO) {
        UserEntity entity = new UserEntity();
        entity.setUsername(createUserDTO.getUsername());
        entity.setNickname(createUserDTO.getNickname());
        entity.setRole(createUserDTO.getRole());
        entity.setStatus(createUserDTO.getStatus());

        UserEntity savedEntity = userRepository.save(entity);

        return toVO(savedEntity);
    }

    /**
     * Update an existing user by ID.
     */
    public UserVO updateUser(Long id, UpdateUserDTO updateUserDTO) {
        Optional<UserEntity> optionalUser = userRepository.findById(id);

        if (!optionalUser.isPresent()) {
            return null;
        }

        UserEntity entity = optionalUser.get();
        entity.setUsername(updateUserDTO.getUsername());
        entity.setNickname(updateUserDTO.getNickname());
        entity.setRole(updateUserDTO.getRole());
        entity.setStatus(updateUserDTO.getStatus());

        UserEntity savedEntity = userRepository.save(entity);

        return toVO(savedEntity);
    }

    /**
     * Update only the user's status.
     *
     * This is used by enable/disable buttons, so the frontend does not need to
     * submit username, nickname and role again.
     */
    public UserVO updateUserStatus(Long id, String status) {
        Optional<UserEntity> optionalUser = userRepository.findById(id);

        if (!optionalUser.isPresent()) {
            return null;
        }

        UserEntity entity = optionalUser.get();
        entity.setStatus(status);

        UserEntity savedEntity = userRepository.save(entity);

        return toVO(savedEntity);
    }

    /**
     * Delete a user by ID.
     */
    public boolean deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            return false;
        }

        userRepository.deleteById(id);

        return true;
    }

    /**
     * Convert database Entity to frontend VO.
     *
     * Entity belongs to the database layer. VO belongs to the API response layer.
     */
    private UserVO toVO(UserEntity entity) {
        return new UserVO(
                entity.getId(),
                entity.getUsername(),
                entity.getNickname(),
                entity.getRole(),
                entity.getStatus()
        );
    }
}
