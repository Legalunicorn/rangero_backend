package com.hiroc.rangero.email;

import com.hiroc.rangero.email.dto.EmailRequest;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;


@Getter
@Setter
public class EmailEvent extends ApplicationEvent {

    private EmailRequest request;

    public EmailEvent(Object source, @Valid EmailRequest request){
        super(source);
        this.request = request;
    }


}
