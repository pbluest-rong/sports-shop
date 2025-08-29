package com.pblues.sportsshop.service;

import com.pblues.sportsshop.config.security.JwtService;
import com.pblues.sportsshop.constant.Role;
import com.pblues.sportsshop.exception.*;
import com.pblues.sportsshop.model.OTP;
import com.pblues.sportsshop.model.User;
import com.pblues.sportsshop.repository.OTPRepository;
import com.pblues.sportsshop.repository.UserRepository;
import com.pblues.sportsshop.dto.request.AuthenticationRequest;
import com.pblues.sportsshop.dto.request.ResetPasswordRequest;
import com.pblues.sportsshop.dto.request.SignupRequest;
import com.pblues.sportsshop.dto.response.AuthenticationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final int REQUEST_COUNT_MAX = 5;

    private final UserRepository userRepository;
    private final OTPRepository otpRepository;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean isExistUser(String email) {
        return userRepository.findByEmail(email).orElse(null) != null;
    }

    @Override
    public void sendOTP(String email) {
        String generatedToken = generateActivationCode(6);
        int minutes = 10;

        OTP otp = otpRepository.findByEmail(email).orElse(null);
        if (otp != null) {
            if (otp.getRequestCount() >= REQUEST_COUNT_MAX) {
                if (!otp.getCreatedAt().toLocalDate().equals(LocalDate.now())) otp.setRequestCount(0);
                else throw new OTPExceededLimitException("OTP limit exceeded. Please try again tomorrow.");
            }
            otp.setOtp(generatedToken);
            otp.setCreatedAt(LocalDateTime.now());
            otp.setExpiresAt(LocalDateTime.now().plusMinutes(minutes));
            otp.setRequestCount(otp.getRequestCount() + 1);
        } else {
            otp = OTP.builder().otp(generatedToken).email(email).createdAt(LocalDateTime.now()).expiresAt(LocalDateTime.now().plusMinutes(minutes)).build();
        }
        otpRepository.save(otp);
        sendOTPToEmail(email, generatedToken, minutes);
    }

    private void sendOTPToEmail(String email, String code, long minutes) {
        String body = """
                <div style="font-family: Arial, sans-serif; padding: 20px; background-color: #f4f4f4;">
                    <div style="max-width: 600px; margin: auto; background: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1);">
                        <h2 style="color: #333333;">🔒 Xác thực Email</h2>
                        <p>Xin chào,</p>
                        <p>Chúng tôi đã nhận được yêu cầu xác thực email từ bạn. Vui lòng sử dụng mã bên dưới để hoàn tất quá trình đăng ký:</p>
                        <div style="font-size: 24px; font-weight: bold; color: #4CAF50; padding: 10px 0;">%s</div>
                        <p><b>Lưu ý:</b> Mã xác thực chỉ có hiệu lực trong vòng <span style="color: red;">%d phút</span> và chỉ sử dụng được <span style="color: red;">1 lần</span>.</p>
                        <p>Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email này.</p>
                        <p>Trân trọng,<br/>Sports Shop</p>
                    </div>
                </div>
                """.formatted(code, minutes);
        try {
            emailService.sendMail(email, "Xác thực Email", body);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String generateActivationCode(int length) {
        String characters = "0123456789";
        StringBuilder code = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            code.append(randomIndex);
        }
        return code.toString();
    }

    @Override
    public void signup(SignupRequest request) {
        if (isExistUser(request.getEmail()))
            throw new AlreadyExistsException("Email already exists");

        OTP otp = otpRepository.findByEmail(request.getEmail())
                .orElseThrow(InvalidOTPException::new);

        if (otp.getExpiresAt().isBefore(LocalDateTime.now()) || !request.getOtp().equals(otp.getOtp())) {
            throw new InvalidOTPException();
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setRole(Role.USER);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        if (request.isAcceptMarketing()) {
            user.setAcceptedMarketing(true);
            user.setAcceptedMarketingAt(LocalDateTime.now());
        }
        userRepository.save(user);
    }

    @Override
    public AuthenticationResponse login(AuthenticationRequest request) {
        try {
            var auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
            var claims = new HashMap<String, Object>();
            var user = (User) auth.getPrincipal();

            var jwtToken = jwtService.generateToken(claims, user);
            var refreshToken = jwtService.generateRefreshToken(user);

            return AuthenticationResponse.builder()
                    .accessToken(jwtToken)
                    .refreshToken(refreshToken)
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .gender(user.getGender())
                    .phone(user.getPhone())
                    .dob(user.getDob())
                    .role(user.getRole())
                    .build();
        } catch (BadCredentialsException ex) {
            throw new InvalidCredentialsException("Bad credentials");
        } catch (LockedException ex) {
            throw new AccountLockedException("User account is locked");
        }
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        if (!isExistUser(request.getEmail()))
            throw new ResourceNotFoundException("Email doesn't exist");

        OTP otp = otpRepository.findByEmail(request.getEmail())
                .orElseThrow(InvalidOTPException::new);

        if (otp.getExpiresAt().isBefore(LocalDateTime.now()) || !request.getOtp().equals(otp.getOtp())) {
            throw new InvalidOTPException();
        }
        User user = new User();
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
    }
}
