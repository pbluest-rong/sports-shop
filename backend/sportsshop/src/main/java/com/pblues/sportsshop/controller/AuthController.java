package com.pblues.sportsshop.controller;

import com.pblues.sportsshop.common.exception.OTPExceededLimitException;
import com.pblues.sportsshop.dto.request.AuthenticationRequest;
import com.pblues.sportsshop.dto.request.ResetPasswordRequest;
import com.pblues.sportsshop.dto.request.SignupRequest;
import com.pblues.sportsshop.dto.response.ApiResponse;
import com.pblues.sportsshop.dto.response.AuthenticationResponse;
import com.pblues.sportsshop.service.user.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @GetMapping("/exists")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkExistUser(@RequestParam("email") @Email @NotEmpty(message = "Email is mandatory") String email) {
        boolean isExist = userService.isExistUser(email);
        Map<String, Boolean> result = new HashMap<>();
        result.put("isExist", isExist);
        return ResponseEntity.ok(ApiResponse.success(
                isExist ? "Email already exists" : "Email is available",
                result
        ));
    }

    @PostMapping("/send-otp")
    public ResponseEntity<ApiResponse> sendOTP(@RequestParam("email") @Email @NotEmpty(message = "Email is mandatory") String email) {
        try {
            userService.sendOTP(email);
            return ResponseEntity.ok(ApiResponse.success("Send OTP successfully", null));
        } catch (OTPExceededLimitException e) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> signUp(@RequestBody @Valid SignupRequest request) {
        userService.signup(request);
        return ResponseEntity.ok(ApiResponse.success("Signup Successful", null));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> login(@RequestBody @Valid AuthenticationRequest request) {
        AuthenticationResponse auth = userService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login Successful", auth));
    }

    @PatchMapping("reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@RequestBody @Valid ResetPasswordRequest request){
        userService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success("Reset password Successful", null));
    }
}