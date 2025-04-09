package com.hiroc.rangero.authentication;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Slf4j
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public AuthenticationResponse login(@Valid @RequestBody LoginRequest request){
        return authenticationService.login(request);
    }


    //TODO replace this with ->
//    @PostMapping("/register")
//    @ResponseStatus(HttpStatus.CREATED)
//    public AuthenticationResponse register(@Valid @RequestBody RegisterRequest request){
//        return authenticationService.register(request);
//    }

    //register + verify should be enough for most cases
    // if the user fails otp and wants a new one
    // register -> verify (fail) -> request otp -> verify

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerDisabled(@Valid @RequestBody RegisterRequest request){
        authenticationService.registerDisabled(request);
    }

    @PostMapping("/opt/{email}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void requestOTP(@PathVariable String email){
        authenticationService.requestOTP(email);
    }

    @PostMapping("/verification/{email}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public AuthenticationResponse verifyAccount(
            @PathVariable String email,
            @RequestBody Integer opt
    ){
        return authenticationService.verifyAccount(email,opt);
    }


}
