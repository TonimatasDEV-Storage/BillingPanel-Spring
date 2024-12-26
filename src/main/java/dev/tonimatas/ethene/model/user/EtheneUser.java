package dev.tonimatas.ethene.model.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
    @Column(nullable = false)
    private boolean verified = false;

    public EtheneUser(Long id, String firstname, String lastname, String email, String password, String role, boolean verified) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.role = role;
        this.verified = verified;
    }

    public EtheneUser() {
    }
}
