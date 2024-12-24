package dev.tonimatas.ethene.security;

import dev.tonimatas.ethene.users.EtheneUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final EtheneUserService userService;
    private final DataSource dataSource;

    public SecurityConfig(EtheneUserService userService, DataSource dataSource) {
        this.userService = userService;
        this.dataSource = dataSource;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return userService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(httpForm -> httpForm
                        .permitAll()
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true))
                .authorizeHttpRequests(registry -> registry
                        .requestMatchers("/register", "/css/**", "/js/**")
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                .rememberMe(config -> config
                        .tokenRepository(persistentTokenRepository())
                        .key("test")
                        .alwaysRemember(true)
                        .userDetailsService(userDetailsService())
                ).logout(config -> config.logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID", "remember-me"))
                .build();
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);

        if (tokenRepository.getJdbcTemplate() != null) {
            String query = "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = ?";
            Integer count = tokenRepository.getJdbcTemplate().queryForObject(query, Integer.class, "persistent_logins");
            boolean databaseExits = count != null && count > 0;

            tokenRepository.setCreateTableOnStartup(!databaseExits);
        }

        return tokenRepository;
    }
}
