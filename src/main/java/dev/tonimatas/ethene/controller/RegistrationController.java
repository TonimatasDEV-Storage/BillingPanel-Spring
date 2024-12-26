package dev.tonimatas.ethene.controller;

import dev.tonimatas.ethene.model.user.EtheneUser;
import dev.tonimatas.ethene.model.user.EtheneUserRepository;
import dev.tonimatas.ethene.model.verify.VerifyCode;
import dev.tonimatas.ethene.model.verify.VerifyCodeRepository;
import dev.tonimatas.ethene.services.EmailService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegistrationController {
    private final EtheneUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final VerifyCodeRepository verifyCodeRepository;

    public RegistrationController(EtheneUserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService, VerifyCodeRepository verifyCodeRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.verifyCodeRepository = verifyCodeRepository;
    }

    @PostMapping(value = "/register", consumes = "application/json")
    public ResponseEntity<?> createUser(@RequestBody EtheneUser user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setVerified(false);
            EtheneUser savedUser = userRepository.save(user);
            VerifyCode verifyCode = new VerifyCode();
            verifyCode.setEmail(user.getEmail());
            verifyCodeRepository.save(verifyCode);

            emailService.sendVerificationEmail(user, verifyCode);
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
        EtheneUser repoUser = userRepository.findByEmail(user.getEmail()).orElse(null);
        VerifyCode verifyCode = verifyCodeRepository.findByEmail(user.getEmail()).orElse(null);

        if (verifyCode != null && repoUser != null) {
            if (verifyCode.getVerificationCode() == request.code || repoUser.isVerified()) {
                user.setVerified(true);
                repoUser.setVerified(true);
                userRepository.saveAndFlush(repoUser);
                verifyCodeRepository.delete(verifyCode);
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid verification code.");
            }
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
    }

    public record CodeRequest(int code) {
    }
}
