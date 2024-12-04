package com.james.spring_security_configuration.services;

import com.james.spring_security_configuration.dtos.requests.BaseRequest;
import com.james.spring_security_configuration.dtos.requests.LoginRequest;
import com.james.spring_security_configuration.dtos.requests.RegistrationRequest;
import com.james.spring_security_configuration.dtos.responses.ApiResponse;
import com.james.spring_security_configuration.dtos.responses.LoginResponse;
import org.springframework.http.ResponseEntity;

public interface UserService {
    ResponseEntity<ApiResponse> register(RegistrationRequest dto);

    ResponseEntity<ApiResponse> resendCode(BaseRequest baseRequest);

    ResponseEntity<ApiResponse<LoginResponse>> activate(String otp, BaseRequest baseRequest);

    ResponseEntity<ApiResponse<LoginResponse>> login(LoginRequest dto);

}
