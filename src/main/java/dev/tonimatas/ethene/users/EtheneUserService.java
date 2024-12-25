package dev.tonimatas.ethene.users;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EtheneUserService implements UserDetailsService {
    private final EtheneUserRepository repository;

    public EtheneUserService(EtheneUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<EtheneUser> user = repository.findByEmail(username);

        if (user.isPresent()) {
            return new EtheneUserDetails(user.get());
        } else {
            throw new UsernameNotFoundException(username);
        }
    }


}
