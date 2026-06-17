package com.example.admin.user.service;

import com.example.admin.common.PageResult;
import com.example.admin.common.BusinessException;
import com.example.admin.user.constant.UserConstants;
import com.example.admin.user.dto.CreateUserDTO;
import com.example.admin.user.dto.UpdateUserDTO;
import com.example.admin.user.entity.UserEntity;
import com.example.admin.user.repository.UserRepository;
import com.example.admin.user.vo.OptionVO;
import com.example.admin.user.vo.UserStatsVO;
import com.example.admin.user.vo.UserVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
     * BCrypt 密码工具，用来把新用户默认密码保存成密文。
     */
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * Constructor injection makes required dependencies clear.
     */
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Query users from the database with filters and pagination.
     *
     * The frontend page starts from 1, but Spring Data JPA page index starts from 0.
     */
    public PageResult<UserVO> listUsers(String keyword, String role, String status, Integer page, Integer size) {
        int safePage = page == null || page < 1 ? 1 : page;
        int safeSize = size == null || size < 1 ? 5 : size;

        Pageable pageable = PageRequest.of(
                safePage - 1,
                safeSize,
                Sort.by(Sort.Direction.ASC, "id")
        );

        Page<UserEntity> entityPage = userRepository.searchUsers(keyword, role, status, pageable);
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
     * Query summary numbers for the user dashboard.
     *
     * These values come from database count queries, not from the current page list.
     * That means the statistics still represent all users even when the table is paginated.
     */
    public UserStatsVO getUserStats() {
        long totalCount = userRepository.count();
        long enabledCount = userRepository.countByStatus(UserConstants.STATUS_ENABLED);
        long disabledCount = userRepository.countByStatus(UserConstants.STATUS_DISABLED);
        long superAdminCount = userRepository.countByRole(UserConstants.ROLE_SUPER_ADMIN);
        long operatorCount = userRepository.countByRole(UserConstants.ROLE_OPERATOR);
        long readonlyCount = userRepository.countByRole(UserConstants.ROLE_READONLY);

        return new UserStatsVO(
                totalCount,
                enabledCount,
                disabledCount,
                superAdminCount,
                operatorCount,
                readonlyCount
        );
    }

    /**
     * Query role options used by frontend select boxes.
     *
     * For now the roles are fixed in code. Later, when we learn role management,
     * this method can be changed to read options from a role table.
     */
    public List<OptionVO> listRoleOptions() {
        List<OptionVO> roles = new ArrayList<OptionVO>();
        roles.add(new OptionVO(UserConstants.ROLE_SUPER_ADMIN, UserConstants.ROLE_SUPER_ADMIN));
        roles.add(new OptionVO(UserConstants.ROLE_OPERATOR, UserConstants.ROLE_OPERATOR));
        roles.add(new OptionVO(UserConstants.ROLE_READONLY, UserConstants.ROLE_READONLY));

        return roles;
    }

    /**
     * Query status options used by frontend select boxes.
     *
     * value keeps the backend/database value. label is the Chinese text shown on the page.
     */
    public List<OptionVO> listStatusOptions() {
        List<OptionVO> statuses = new ArrayList<OptionVO>();
        statuses.add(new OptionVO(UserConstants.STATUS_ENABLED, UserConstants.STATUS_ENABLED_LABEL));
        statuses.add(new OptionVO(UserConstants.STATUS_DISABLED, UserConstants.STATUS_DISABLED_LABEL));

        return statuses;
    }

    /**
     * Create a new user and save it to the database.
     */
    public UserVO createUser(CreateUserDTO createUserDTO) {
        validateUsernameNotUsed(createUserDTO.getUsername());

        UserEntity entity = new UserEntity();
        entity.setUsername(createUserDTO.getUsername());
        entity.setNickname(createUserDTO.getNickname());
        entity.setPassword(passwordEncoder.encode("123456"));
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

        validateUsernameNotUsedByOtherUser(updateUserDTO.getUsername(), id);

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
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    /**
     * Validate username before creating a user.
     *
     * Username is a business identity, so two users cannot use the same value.
     */
    private void validateUsernameNotUsed(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new BusinessException(400, "账号已存在");
        }
    }

    /**
     * Validate username before updating a user.
     *
     * Editing allows keeping the current user's username, but blocks using
     * another user's username.
     */
    private void validateUsernameNotUsedByOtherUser(String username, Long currentUserId) {
        if (userRepository.existsByUsernameAndIdNot(username, currentUserId)) {
            throw new BusinessException(400, "账号已存在");
        }
    }
}
