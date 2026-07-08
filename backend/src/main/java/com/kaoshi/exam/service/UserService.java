package com.kaoshi.exam.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kaoshi.exam.entity.User;
import com.kaoshi.exam.mapper.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public User findByUsername(String username) {
        return userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
    }

    public User findById(Long id) {
        return userMapper.selectById(id);
    }

    public User register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("STUDENT");
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.insert(user);
        return user;
    }

    public void resetPassword(Long userId, String newPassword) {
        User user = findById(userId);
        if (user != null) {
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setUpdateTime(LocalDateTime.now());
            userMapper.updateById(user);
        }
    }

    public boolean authenticate(String username, String password) {
        User user = findByUsername(username);
        return user != null && passwordEncoder.matches(password, user.getPassword());
    }

    public void update(User user) {
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }
}