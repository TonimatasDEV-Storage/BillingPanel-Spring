package dev.tonimatas.ethene.users;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class EtheneUserDetails extends EtheneUser implements UserDetails {
    private static final List<GrantedAuthority> ROLE_USER = Collections.unmodifiableList(AuthorityUtils.createAuthorityList("ROLE_USER"));

    public EtheneUserDetails(EtheneUser user) {
        super(user.getId(), user.getFirstname(), user.getLastname(), user.getEmail(), user.getPassword(), user.getRole(), user.getVerificationCode());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return ROLE_USER;
    }

    @Override
    public String getUsername() {
        return getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
