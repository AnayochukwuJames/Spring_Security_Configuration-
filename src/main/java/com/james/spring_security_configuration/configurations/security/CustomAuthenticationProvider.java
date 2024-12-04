package com.james.spring_security_configuration.configurations.security;

import com.james.spring_security_configuration.configurations.security.jwt.UserModelDetailsService;
import com.james.spring_security_configuration.enums.AuthProvider;
import com.james.spring_security_configuration.exceptions.BadRequestException;
import com.james.spring_security_configuration.models.User;
import com.james.spring_security_configuration.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserModelDetailsService userModelDetailsService;

    private final UserRepository userRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        CustomAuthenticationToken authenticationToken = (CustomAuthenticationToken) authentication;
        String principal = authenticationToken.getCredentials().toString();
        String password = authenticationToken.getPassword() != null ? authenticationToken.getPassword() : "";

        if (AuthProvider.activation.equals(authenticationToken.getAuthProvider())) {
            return userEmailAuthentication(principal);
        }
        return userEmailAndPasswordAuthentication(principal, password);
    }

    private Authentication userEmailAuthentication(String principal) {
        User user = userRepository.findFirstByEmail(principal)
                .orElseThrow(() -> new BadRequestException("Authentication failed"));
        List<GrantedAuthority> grantedAuthorities = List.of(new SimpleGrantedAuthority(user.getRole().name()));
        return new UsernamePasswordAuthenticationToken(principal, null, grantedAuthorities);
    }

    private Authentication userEmailAndPasswordAuthentication(String principal, String password) {
        User user = userRepository.findFirstByEmail(principal)
                .orElseThrow(() -> new BadRequestException("Authentication failed"));

        if (!userModelDetailsService.getPasswordEncoder().matches(password, user.getPassword())) {
            throw new BadRequestException("Invalid credentials");
        }

        List<GrantedAuthority> grantedAuthorities = List.of(new SimpleGrantedAuthority(user.getRole().name()));
        return new UsernamePasswordAuthenticationToken(principal, null, grantedAuthorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return CustomAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
