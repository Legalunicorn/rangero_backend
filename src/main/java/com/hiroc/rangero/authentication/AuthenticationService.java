package com.hiroc.rangero.authentication;


import com.hiroc.rangero.email.EmailEvent;
import com.hiroc.rangero.email.dto.EmailRequest;
import com.hiroc.rangero.email.enums.EmailType;
import com.hiroc.rangero.exception.BadRequestException;
import com.hiroc.rangero.user.*;
import com.hiroc.rangero.utility.JwtService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    //Registered a disabled account
    @Transactional
    public void registerDisabled(RegisterRequest request){

        //Password patches
        if (!request.getPassword().equals(request.getConfirmPassword())){
            throw new BadCredentialsException("Passwords do not match");
        }

        //Check if VALID user exist
        User newUser = userRepository.findByEmail(request.getEmail());
        if (newUser!=null && newUser.isEnabled()){
            //valid, enabled user
            log.debug("registration failed - email in use");
            throw new BadRequestException("Email is already taken");
        }

        User toSave;
        //If newUser is null create it
        if (newUser==null) {
            toSave = User.builder().build();
        } else{
            toSave = newUser;
        }
        toSave.setUsername(request.getUsername());
        toSave.setEmail(request.getEmail());
        toSave.setPassword(passwordEncoder.encode(request.getPassword()));
        toSave.setEnabled(false);
        toSave.setRole(UserRole.USER);
        if (toSave.getRegistrationOTPRateLimit()==null) toSave.setRegistrationOTPRateLimit(new RegistrationOTPRateLimit());
        userRepository.save(toSave);

        //Should automatically generate OTP
        requestOTP(toSave.getEmail());

    }

    @Transactional
    public void requestOTP(String email){
        //1. check is the user exist and is not enabled
        User user = userRepository.findByEmail(email);
        if (user==null){
            throw new BadRequestException("User does not exist");
        }
        if (user.isEnabled()){
            throw new BadRequestException("User is already verified");
        }

        //2. check if user has exceeded the OTP rate limit
        if (user.getRegistrationOTPRateLimit().isRateLimited()){
            throw new BadCredentialsException("You have exceed the number of OTP generation attempted. Try again in 5 minutes");
        }
        user.getRegistrationOTPRateLimit().addAttempt();

        //3. Generate an OTP
        int randomOtp = generateRandomOtp();
        log.info(">>> OTP : {}",randomOtp);

        RegistrationOTP otp = RegistrationOTP.builder()
                .otpCode(randomOtp)
                .expireTime(LocalDateTime.now().plusMinutes(10))
                .build();

        user.setRegistrationOTP(otp);
        userRepository.save(user);
        //TODO 4. Send the OTP to the user email
        EmailRequest request  = EmailRequest.builder()
                .recipient(user.getEmail())
                .body("""
                        Dear %s,
                        %s is your one-time passcode (OTP) for your account registration.
                        You can copy paste the code or enter it manually in the website.
                        This code will be valid for 10 minutes.
                        
                        Enjoy the app!
                        Rangero Team
                        """.formatted(user.getUsername(),randomOtp))
                .subject("Your One-Time-Password (OTP) For Rangero Registration")
                .build();

        eventPublisher.publishEvent(new EmailEvent(this,request));
    }



    //TODO - also need to rate limit the verification attempts
    @Transactional
    public AuthenticationResponse verifyAccount(String email, int inputOTP){
        //1. get the user base on the request
        User user = userRepository.findByEmail(email);
        if (user==null) throw new BadRequestException("user does not exist");
        if (user.isEnabled()) throw new BadRequestException("User is already verified");

        RegistrationOTP otp = user.getRegistrationOTP();
        if (otp==null) throw new BadRequestException("Verified failed. Try again");
        if (otp.getExpireTime().isBefore(LocalDateTime.now())){
            throw new BadCredentialsException("OTP has expired please try again");
        }


        if (otp.getOtpCode()!=inputOTP) throw new BadRequestException("OTP does not match. Please try again");
        //3. if everything passed generated a token
        user.setEnabled(true);
        String token = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .token(token)
                .build();


    }

    private Integer generateRandomOtp(){
        int min = 110_011;
        int max = 989_999;
        return (int) (Math.random()*(max-min)+min);
    }

    public AuthenticationResponse login( LoginRequest request){
        User user = userRepository.findByEmail(request.getEmail());
        if (user==null || !passwordEncoder.matches(request.getPassword(),user.getPassword())){
            log.info("LOGIN FAILED");
            throw new BadCredentialsException("Email or password is incorrect");
        }
        String token = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }


}
