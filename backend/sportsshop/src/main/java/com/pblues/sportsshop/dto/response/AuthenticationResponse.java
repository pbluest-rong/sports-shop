package com.pblues.sportsshop.dto.response;

import com.pblues.sportsshop.common.constant.Role;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class AuthenticationResponse {
    private String accessToken;
    private String email;
    private String fullName;
    private Boolean gender;
    private String phone;
    private LocalDate dob;
    private Role role;
}