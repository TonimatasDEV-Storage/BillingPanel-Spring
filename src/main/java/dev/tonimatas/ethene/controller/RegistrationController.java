package dev.tonimatas.ethene.controller;

import dev.tonimatas.ethene.users.EtheneUser;
import dev.tonimatas.ethene.users.EtheneUserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegistrationController {
    private final EtheneUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationController(EtheneUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping(value = "/register", consumes = "application/json")
    public ResponseEntity<?> createUser(@RequestBody EtheneUser user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            EtheneUser savedUser = userRepository.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists. Please use a different one.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }
}
