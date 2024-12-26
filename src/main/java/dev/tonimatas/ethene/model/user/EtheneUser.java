package dev.tonimatas.ethene.model.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.security.SecureRandom;

@Getter
@Setter
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = "email")})
public class EtheneUser {
    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false)
    private String firstname;
    @Column(nullable = false)
    private String lastname;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String role = "user";
    private Integer verificationCode = generateVerificationCode();

    public EtheneUser(Long id, String firstname, String lastname, String email, String password, String role, Integer verificationCode) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.role = role;
        this.verificationCode = verificationCode;
    }

    public EtheneUser() {
    }
    
    public Integer generateVerificationCode() {
        return new SecureRandom().nextInt(100000, 999999);
    }
}
