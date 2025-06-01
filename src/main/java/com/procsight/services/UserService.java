package com.procsight.services;

import com.procsight.models.User;
import com.procsight.repositories.UserRepository;
import com.procsight.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> getUserByUid(String uid) {
        return userRepository.findByUid(uid);
    }

    public User getUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }

    public List<User> getUsersByCompanyId(String companyId) {
        return userRepository.findByCompanyId(companyId);
    }

    public List<User> getActiveUsersByCompanyId(String companyId) {
        return userRepository.findActiveUsersByCompanyId(companyId);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateLastLogin(String uid) {
        User user = getUserByUid(uid)
                .orElseThrow(() -> new UserNotFoundException("User not found with uid: " + uid));
        user.setLastLogin(LocalDateTime.now());
        return userRepository.save(user);
    }

    public User updateUser(String id, User userDetails) {
        User user = getUserById(id);
        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setDepartment(userDetails.getDepartment());
        user.setCostCenter(userDetails.getCostCenter());
        user.setApprovalLimit(userDetails.getApprovalLimit());
        user.setRole(userDetails.getRole());
        return userRepository.save(user);
    }

    public void deleteUser(String id) {
        User user = getUserById(id);
        user.setIsActive(false);
        userRepository.save(user);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsByUid(String uid) {
        return userRepository.existsByUid(uid);
    }
}