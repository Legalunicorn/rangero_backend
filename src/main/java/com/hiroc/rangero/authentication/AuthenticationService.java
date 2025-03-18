package com.hiroc.rangero.authentication;


import com.hiroc.rangero.exception.BadRequestException;
import com.hiroc.rangero.user.User;
import com.hiroc.rangero.user.UserRepository;
import com.hiroc.rangero.user.UserRole;
import com.hiroc.rangero.utility.JwtService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    //TODO two step process check if email exist first
//    public boolean checkEmail(@RequestBody String email){
//        //check if email exists
//
//    }

    public AuthenticationResponse register( RegisterRequest request){
        //Ensure password matches
        if (!request.getPassword().equals(request.getConfirmPassword())){
            throw new BadCredentialsException("Passwords do not match");
        }
        //Check the username and email is unique
        User exists = userRepository.findByEmail(request.getEmail());

        if (exists!=null){
            //TODO throw a proper error
            log.debug("Registration failed - user with email exists");
            throw new BadRequestException("Email is already taken");
        }

        //Create user
        User newUser = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.USER)
                .build();

        userRepository.save(newUser);

        //Generate the token
        String token = jwtService.generateToken(newUser);
        return AuthenticationResponse.builder()
                .token(token)
                .username(request.getUsername())
                .email(request.getEmail())
                .build();

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
