package com.hiroc.rangero.email;

import com.hiroc.rangero.email.dto.EmailRequest;
import com.hiroc.rangero.email.enums.EmailType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("$spring.mail.username")
    private String applicationEmail;


    @TransactionalEventListener
    @Async
    public void sendEmail(EmailEvent event){
        EmailRequest request = event.getRequest();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(applicationEmail);
        message.setTo(request.getRecipient());
        message.setText(request.getBody());

        if (request.getEmailType()== EmailType.NOTIFICATION){
            message.setSubject("Notification");
        } else if (request.getEmailType()== EmailType.REGISTRATION){
            message.setSubject("OTP for Registration");
        } else {
            message.setSubject("Rangero");
        }

        mailSender.send(message);
    }


}
