
package com.kaoshi.exam.controller;

import com.kaoshi.exam.dto.LoginDTO;
import com.kaoshi.exam.dto.RegisterDTO;
import com.kaoshi.exam.entity.User;
import com.kaoshi.exam.service.UserService;
import com.kaoshi.exam.utils.JwtUtils;
import com.kaoshi.exam.utils.Result;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtils jwtUtils;

    public AuthController(UserService userService, JwtUtils jwtUtils) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginDTO dto) {
        if (!userService.authenticate(dto.getUsername(), dto.getPassword())) {
            return Result.error(401, "用户名或密码错误");
        }
        
        User user = userService.findByUsername(dto.getUsername());
        String token = jwtUtils.generateToken(user.getId(), user.getUsername(), user.getRole());
        
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", user);
        
        return Result.success(data);
    }

    @PostMapping("/register")
    public Result<User> register(@RequestBody RegisterDTO dto) {
        if (userService.findByUsername(dto.getUsername()) != null) {
            return Result.error(400, "用户名已存在");
        }
        
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        
        User registered = userService.register(user);
        return Result.success("注册成功", registered);
    }

    @GetMapping("/me")
    public Result<User> getCurrentUser(@AuthenticationPrincipal User user) {
        return Result.success(user);
    }

    @PostMapping("/reset-admin")
    public Result<String> resetAdminPassword() {
        User admin = userService.findByUsername("admin");
        if (admin != null) {
            userService.resetPassword(admin.getId(), "admin123");
            return Result.success("管理员密码已重置为: admin123");
        }
        return Result.error(404, "管理员不存在");
    }
}
