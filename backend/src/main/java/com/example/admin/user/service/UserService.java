package com.example.admin.user.service;

import com.example.admin.user.dto.CreateUserDTO;
import com.example.admin.user.dto.UpdateUserDTO;
import com.example.admin.user.entity.UserEntity;
import com.example.admin.user.repository.UserRepository;
import com.example.admin.user.vo.UserVO;
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
     * Query users from the database.
     *
     * keyword is used to search username or nickname.
     * status is used to filter enabled or disabled users.
     */
    public List<UserVO> listUsers(String keyword, String status) {
        List<UserEntity> entities = userRepository.searchUsers(keyword, status);
        List<UserVO> users = new ArrayList<UserVO>();

        for (UserEntity entity : entities) {
            users.add(toVO(entity));
        }

        return users;
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
     * Keeping this conversion explicit helps beginners see the boundary clearly.
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
