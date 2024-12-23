package dev.tonimatas.ethene.model;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class EtheneUserService implements UserDetailsService {
    @Autowired
    private EtheneUserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<EtheneUser> user = repository.findByUsername(username);
        
        if (user.isPresent()) {
            EtheneUser etheneUser = user.get();
            return User.builder()
                    .username(etheneUser.getUsername())
                    .password(etheneUser.getPassword())
                    .build();
        } else {
            throw new UsernameNotFoundException(username);
        }
    }
}
