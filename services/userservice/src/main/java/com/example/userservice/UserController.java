package com.example.userservice;

import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class UserController {

    private final UserRepository repo;

    public UserController(UserRepository repo) {
        this.repo = repo;
    }

    @PostMapping("/users")
    public String create(@RequestBody User user) {
        repo.save(user);
        return "User created";
    }

    @PostMapping("/users/validate")
    public boolean validate(@RequestBody User user) {
        return repo.findByUsernameAndPassword(
                user.getUsername(),
                user.getPassword()
        ).isPresent();
    }
}
