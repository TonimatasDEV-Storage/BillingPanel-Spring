package dev.tonimatas.ethene.component;

import dev.tonimatas.ethene.model.user.EtheneUser;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class VerificationCheckFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (servletRequest instanceof HttpServletRequest request && servletResponse instanceof HttpServletResponse response) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null) {
                if (authentication.getPrincipal() instanceof EtheneUser user) {
                    if (user.getVerificationCode() != null) {
                        if (!request.getRequestURI().contains("verify")) {
                            response.sendRedirect("/verify");
                            return;
                        }
                    }
                }
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
