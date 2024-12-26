package dev.tonimatas.ethene.model.verify;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.security.SecureRandom;

@Setter
@Getter
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = "email")})
public class VerifyCode {
    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private int verificationCode = generateVerificationCode();

    public static int generateVerificationCode() {
        return new SecureRandom().nextInt(100000, 999999);
    }
}
