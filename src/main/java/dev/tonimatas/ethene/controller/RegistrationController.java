package dev.tonimatas.ethene.controller;

import dev.tonimatas.ethene.services.EmailService;
import dev.tonimatas.ethene.users.EtheneUser;
import dev.tonimatas.ethene.users.EtheneUserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegistrationController {
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

            String subject = "Account Verification";
            String verificationCode = "VERIFICATION CODE " + user.getVerificationCode();
            String htmlMessage = "<html>"
                    + "<body style=\"font-family: Arial, sans-serif;\">"
                    + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                    + "<h2 style=\"color: #333;\">Welcome to our app!</h2>"
                    + "<p style=\"font-size: 16px;\">Please enter the verification code below to continue:</p>"
                    + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                    + "<h3 style=\"color: #333;\">Verification Code:</h3>"
                    + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + verificationCode + "</p>"
                    + "</div>"
                    + "</div>"
                    + "</body>"
                    + "</html>";

            emailService.sendEmail(user.getEmail(), subject, htmlMessage);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists. Please use a different one.");
        } catch (MailSendException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error sending the verification email.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }


}
