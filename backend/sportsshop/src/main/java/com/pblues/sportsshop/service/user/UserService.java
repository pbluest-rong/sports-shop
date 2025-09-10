package com.pblues.sportsshop.service.user;

import com.pblues.sportsshop.dto.request.AuthenticationRequest;
import com.pblues.sportsshop.dto.request.ResetPasswordRequest;
import com.pblues.sportsshop.dto.request.SignupRequest;
import com.pblues.sportsshop.dto.response.AuthenticationResponse;

public interface UserService {
    boolean isExistUser(String email);

    void sendOTP(String email);

    void signup(SignupRequest request);

    AuthenticationResponse login(AuthenticationRequest request);

    void resetPassword(ResetPasswordRequest request);
}
