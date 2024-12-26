package dev.tonimatas.ethene.controller;

import dev.tonimatas.ethene.services.EmailService;
import dev.tonimatas.ethene.model.user.EtheneUser;
import dev.tonimatas.ethene.model.user.EtheneUserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class RegistrationController {
    private final Map<String, Integer> attempts = new ConcurrentHashMap<>();
    private final EtheneUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public RegistrationController(EtheneUserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @PostMapping(value = "/register", consumes = "application/json")
    public ResponseEntity<?> createUser(@RequestBody EtheneUser user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            EtheneUser savedUser = userRepository.save(user);

            emailService.sendVerificationEmail(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists. Please use a different one.");
        } catch (MailSendException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error sending the verification email.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    @PostMapping(value = "/verify", consumes = "application/json")
    public ResponseEntity<?> createUser(@RequestBody CodeRequest request, @AuthenticationPrincipal EtheneUser user) {
        EtheneUser repositoryUser = userRepository.findByEmail(user.getEmail()).orElse(null);
        
        if (repositoryUser != null) {
            if (user.getVerificationCode().equals(request.code) && repositoryUser.getPassword().equals(user.getPassword())) {
                repositoryUser.setVerificationCode(null);
                user.setVerificationCode(null);

                userRepository.saveAndFlush(repositoryUser);
                attempts.remove(repositoryUser.getEmail());
                
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                if (attempts.containsKey(user.getEmail())) {
                    int attempt = attempts.get(user.getEmail()) + 1;

                    if (attempt >= 3) {
                        attempts.remove(user.getEmail());
                        
                        Integer newCode = repositoryUser.generateVerificationCode();

                        repositoryUser.setVerificationCode(newCode);
                        user.setVerificationCode(newCode);

                        userRepository.saveAndFlush(repositoryUser);

                        emailService.sendVerificationEmail(repositoryUser);
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Attempt 3/3. New verification code has been sent.");
                    } else {
                        attempts.put(user.getEmail(), attempts.get(user.getEmail()) + 1);
                    }
                } else {
                    attempts.put(user.getEmail(), 1);
                }

                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid verification code. Attempt: " + attempts.get(user.getEmail()) + "/3");
            }
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
    }

    public record CodeRequest(Integer code) {
    }
}
