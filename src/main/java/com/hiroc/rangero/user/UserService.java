package com.hiroc.rangero.user;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    //TODO consider using optional instead
    public User findByEmail(String email){
        return userRepository.findByEmail(email);
    }
}
