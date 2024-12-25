package dev.tonimatas.ethene.users;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class EtheneUserDetails extends EtheneUser implements UserDetails {
    private static final List<GrantedAuthority> ROLE_USER = Collections.unmodifiableList(AuthorityUtils.createAuthorityList("ROLE_USER"));

    public EtheneUserDetails(EtheneUser etheneUser) {
        this.setId(etheneUser.getId());
        this.setEmail(etheneUser.getEmail());
        this.setPassword(etheneUser.getPassword());
        this.setFirstname(etheneUser.getFirstname());
        this.setLastname(etheneUser.getLastname());
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
