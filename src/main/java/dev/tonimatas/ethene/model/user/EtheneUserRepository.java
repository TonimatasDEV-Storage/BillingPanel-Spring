package dev.tonimatas.ethene.model.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EtheneUserRepository extends JpaRepository<EtheneUser, Long> {
    Optional<EtheneUser> findByEmail(String email);
}
