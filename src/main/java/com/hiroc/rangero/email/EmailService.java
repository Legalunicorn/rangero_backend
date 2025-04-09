package com.hiroc.rangero.email;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("$spring.mail.username")
    private String applicationEmail;

    public void sendEmail(String recipient,String subject, String body){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(applicationEmail);
        message.setTo(recipient);
        message.setText(body);
        message.setSubject(subject);

        mailSender.send(message);
    }


    public void sendRegistrationEmail(){

    }

}
