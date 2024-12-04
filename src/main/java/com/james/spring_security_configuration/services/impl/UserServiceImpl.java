package com.james.spring_security_configuration.services.impl;

import com.james.spring_security_configuration.configurations.security.CustomAuthenticationToken;
import com.james.spring_security_configuration.configurations.security.jwt.TokenProvider;
import com.james.spring_security_configuration.dtos.requests.BaseRequest;
import com.james.spring_security_configuration.dtos.requests.LoginRequest;
import com.james.spring_security_configuration.dtos.requests.RegistrationRequest;
import com.james.spring_security_configuration.dtos.responses.ApiResponse;
import com.james.spring_security_configuration.dtos.responses.LoginResponse;
import com.james.spring_security_configuration.enums.AuthProvider; // Use this enum
import com.james.spring_security_configuration.enums.RoleName;
import com.james.spring_security_configuration.exceptions.BadRequestException;
import com.james.spring_security_configuration.exceptions.ForbiddenException;
import com.james.spring_security_configuration.exceptions.NotFoundException;
import com.james.spring_security_configuration.exceptions.PreConditionFailedException;
import com.james.spring_security_configuration.models.User;
import com.james.spring_security_configuration.repositories.UserRepository;
import com.james.spring_security_configuration.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    public ResponseEntity<ApiResponse> register(RegistrationRequest dto) {
        Optional<User> optionalUser = userRepository.findFirstByEmail(dto.getEmail());
        if (optionalUser.isPresent()) {
            throw new BadRequestException("Email already exists");
        }

        User user = createUser(dto);
        ApiResponse responseBody = ApiResponse.builder()
                .responseMessage("User created successfully")
                .build();
        return new ResponseEntity<>(responseBody, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<ApiResponse> resendCode(BaseRequest baseRequest) {
        ApiResponse responseBody = ApiResponse.builder()
                .responseMessage("OTP sent successfully")
                .build();
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ApiResponse<LoginResponse>> activate(String otp, BaseRequest baseRequest) {
        User user = userRepository.findFirstByEmail(baseRequest.getEmail())
                .orElseThrow(() -> new NotFoundException("User not found"));

        user.setEnabled(true);
        user.setActivated(true);
        userRepository.save(user);

        CustomAuthenticationToken authenticationToken = new CustomAuthenticationToken(user.getEmail(), "", AuthProvider.activation);
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.createToken(authentication, false);

        ApiResponse<LoginResponse> responseBody = ApiResponse.<LoginResponse>builder()
                .responseMessage("Account activated successfully")
                .responseBody(new LoginResponse(user, jwt))
                .build();
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ApiResponse<LoginResponse>> login(LoginRequest dto) {
        User user = userRepository.findFirstByEmail(dto.getEmail())
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!user.isActivated()) {
            throw new PreConditionFailedException("Email not verified");
        }

        if (!user.isEnabled()) {
            throw new ForbiddenException("Account suspended. Please contact support");
        }

        CustomAuthenticationToken authenticationToken = new CustomAuthenticationToken(dto.getEmail(), dto.getPassword(), AuthProvider.local);
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        boolean rememberMe = dto.getRememberMe() != null && dto.getRememberMe();
        String jwt = tokenProvider.createToken(authentication, rememberMe);

        user.setLastLoginDate(LocalDateTime.now());
        userRepository.save(user);

        ApiResponse<LoginResponse> responseBody = ApiResponse.<LoginResponse>builder()
                .responseMessage("Login successful")
                .responseBody(new LoginResponse(user, jwt))
                .build();
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    private User createUser(RegistrationRequest dto) {
        User user = new User();
        user.setEmail(dto.getEmail().trim());
        user.setName(dto.getName().trim());
        user.setEnabled(false);
        user.setRole(RoleName.ROLE_USER);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setActivated(false);
        return userRepository.save(user);
    }
}
