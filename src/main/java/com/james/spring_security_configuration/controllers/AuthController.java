package com.james.spring_security_configuration.controllers;

import com.james.spring_security_configuration.dtos.requests.BaseRequest;
import com.james.spring_security_configuration.dtos.requests.LoginRequest;
import com.james.spring_security_configuration.dtos.requests.RegistrationRequest;
import com.james.spring_security_configuration.dtos.responses.ApiResponse;
import com.james.spring_security_configuration.dtos.responses.LoginResponse;
import com.james.spring_security_configuration.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

   private final UserService userService;

   @PostMapping(value = "/register")
   public ResponseEntity<ApiResponse> register(@RequestBody @Valid RegistrationRequest registrationRequest) {
      return userService.register(registrationRequest);
   }

   @PostMapping(value = "/resend-code")
   public ResponseEntity<ApiResponse> resendCode(@RequestBody @Valid BaseRequest baseRequest) {
      return userService.resendCode(baseRequest);
   }

   @PatchMapping(value = "/activate/{otp}")
   public ResponseEntity<ApiResponse<LoginResponse>> activate(@PathVariable String otp, @RequestBody @Valid BaseRequest baseRequest) {
      return userService.activate(otp, baseRequest);
   }

   @PostMapping("/login")
   public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
      return userService.login(loginRequest);
   }
}
