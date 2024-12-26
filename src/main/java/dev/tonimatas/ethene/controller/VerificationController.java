package dev.tonimatas.ethene.controller;

import dev.tonimatas.ethene.users.EtheneUser;
import dev.tonimatas.ethene.users.EtheneUserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VerificationController {
    private final EtheneUserRepository userRepository;

    public VerificationController(EtheneUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping(value = "/verify", consumes = "application/json")
    public ResponseEntity<?> createUser(@RequestBody CodeRequest request, @AuthenticationPrincipal EtheneUser user) {
        if (user.getVerificationCode().equals(request.code)) {
            EtheneUser repositoryUser = userRepository.findByEmail(user.getEmail()).orElse(null);

            if (repositoryUser != null && repositoryUser.getPassword().equals(user.getPassword())) {
                repositoryUser.setVerificationCode(null);
                user.setVerificationCode(null);

                userRepository.saveAndFlush(repositoryUser);

                return new ResponseEntity<>(HttpStatus.OK);

            }
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
    }

    public record CodeRequest(Integer code) {
    }
}
