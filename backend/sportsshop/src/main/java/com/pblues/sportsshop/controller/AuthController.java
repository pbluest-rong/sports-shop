package com.pblues.sportsshop.controller;

import com.pblues.sportsshop.common.constant.ErrorCode;
import com.pblues.sportsshop.common.exception.AppException;
import com.pblues.sportsshop.dto.request.AuthenticationRequest;
import com.pblues.sportsshop.dto.request.ResetPasswordRequest;
import com.pblues.sportsshop.dto.request.SignupRequest;
import com.pblues.sportsshop.dto.response.ApiResponse;
import com.pblues.sportsshop.dto.response.AuthenticationResponse;
import com.pblues.sportsshop.service.user.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
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
        return ResponseEntity.ok(ApiResponse.success(isExist ? "Email already exists" : "Email is available", result));
    }

    @PostMapping("/send-otp")
    public ResponseEntity<ApiResponse> sendOTP(@RequestParam("email") @Email @NotEmpty(message = "Email is mandatory") String email) {
            userService.sendOTP(email);
            return ResponseEntity.ok(ApiResponse.success("Send OTP successfully", null));
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> signUp(@RequestBody @Valid SignupRequest request) {
        userService.signup(request);
        return ResponseEntity.ok(ApiResponse.success("Signup Successful", null));
    }

    @PatchMapping("reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        userService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success("Reset password Successful", null));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> login(@RequestBody @Valid AuthenticationRequest request) {
        Object[] result = userService.login(request);
        String refreshToken = (String) result[0];
        AuthenticationResponse authenticationResponse = (AuthenticationResponse) result[1];
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken).httpOnly(true)
                // .secure(true)
                .path("/auth/refresh").maxAge(7 * 24 * 60 * 60)
                .sameSite("Strict") //Strict, Lax, None
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(ApiResponse.success("Login Successful", authenticationResponse));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setHttpOnly(true);
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                    break;
                }
            }
        }

        return ResponseEntity.ok(ApiResponse.success("Logout Successful", null));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> refreshToken(@CookieValue("refreshToken") String refreshToken) {
        AuthenticationResponse response = userService.getAccessToken(refreshToken);
        return ResponseEntity.ok().body(ApiResponse.success("Refresh Successful", response));
    }

}