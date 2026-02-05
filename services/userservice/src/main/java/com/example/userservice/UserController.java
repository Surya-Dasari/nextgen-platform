package com.example.userservice;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@CrossOrigin
public class UserController {

    private final UserRepository repo;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserController(UserRepository repo) {
        this.repo = repo;
    }

    // Signup
    @PostMapping("/users")
    public String create(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        repo.save(user);
        return "User created";
    }

    // Login validation
    @PostMapping("/users/validate")
    public boolean validate(@RequestBody User user) {
        Optional<User> dbUser = repo.findByUsername(user.getUsername());

        if (dbUser.isEmpty()) {
            return false;
        }

        return passwordEncoder.matches(
                user.getPassword(),
                dbUser.get().getPassword()
        );
    }
}

