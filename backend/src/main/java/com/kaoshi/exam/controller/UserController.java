package com.kaoshi.exam.controller;

import com.kaoshi.exam.entity.User;
import com.kaoshi.exam.service.UserService;
import com.kaoshi.exam.utils.Result;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/me")
    public Result<User> getCurrentUser(@AuthenticationPrincipal User user) {
        return Result.success(user);
    }

    @PutMapping("/update")
    public Result<User> updateUser(@AuthenticationPrincipal User currentUser, @RequestBody User user) {
        User existing = userService.findById(currentUser.getId());
        if (user.getEmail() != null) {
            existing.setEmail(user.getEmail());
        }
        if (user.getPhone() != null) {
            existing.setPhone(user.getPhone());
        }
        if (user.getAvatar() != null) {
            existing.setAvatar(user.getAvatar());
        }
        existing.setUpdateTime(java.time.LocalDateTime.now());
        userService.update(existing);
        return Result.success("更新成功", existing);
    }

    @PostMapping("/change-password")
    public Result<String> changePassword(@AuthenticationPrincipal User user, @RequestBody Map<String, String> data) {
        String oldPassword = data.get("oldPassword");
        String newPassword = data.get("newPassword");
        
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return Result.error(400, "原密码错误");
        }
        
        userService.resetPassword(user.getId(), newPassword);
        return Result.success("密码修改成功");
    }
}