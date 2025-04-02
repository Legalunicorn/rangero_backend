package com.hiroc.rangero.user;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;


    public Set<User> findByEmailIn(Set<String> emails) {
        return userRepository.findByEmailIn(emails);
    }

    //TODO consider using optional instead
    public User findByEmail(String email){
        return userRepository.findByEmail(email);
    }
}
