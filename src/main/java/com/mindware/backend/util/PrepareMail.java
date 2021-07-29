package com.mindware.backend.util;


import com.mindware.backend.entity.Users;
import com.mindware.backend.entity.email.Mail;
import com.mindware.backend.rest.email.MailRestTemplate;
import com.mindware.backend.rest.user.UserRestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class PrepareMail {

    @Autowired
    private  MailRestTemplate mailRestTemplate;


    public  void sendMailCreateUser(Users users, String plainPassword, String login, String comeFrom){
        Mail mail = new Mail();
        mail.setLoginUser(login);
        mail.setSendDate(LocalDateTime.now());
        mail.setNumberRequest(0);
        mail.setMailFrom(comeFrom);
        mail.setMailTo(users.getEmail());
        mail.setMailSubject("Cuenta usuario AUTO FORM");
        mail.setMailContent(String.format("Se creo su cuenta de usuario '%s'  en el sistema AUTO FORM, \n su clave temporal es: %s", login, plainPassword));
        mailRestTemplate.add(mail);
    }

    public  void sendMailResetPassword(Users users, String plainPassword, String login, String comeFrom){
        Mail mail = new Mail();
        mail.setLoginUser(login);
        mail.setSendDate(LocalDateTime.now());
        mail.setNumberRequest(0);
        mail.setMailFrom(comeFrom);
        mail.setMailTo(users.getEmail());
        mail.setMailSubject("Reset password usuario Auto Form");
        mail.setMailContent(String.format("Se reseteo su clave acceso de su cuenta '%s', nueva clave temporal es: %s", users.getLogin(),plainPassword));
        mailRestTemplate.add(mail);
    }
}
