package com.mindware.ui.views.login;

import com.mindware.backend.entity.Users;
import com.mindware.backend.entity.netbank.Gbpmt;
import com.mindware.backend.entity.netbank.dto.AdusrOfi;
import com.mindware.backend.rest.login.JwtRequest;
import com.mindware.backend.rest.login.LoginRestTemplate;
import com.mindware.backend.rest.login.Token;
import com.mindware.backend.rest.netbank.AdusrOfiRestTemplate;
import com.mindware.backend.rest.netbank.GbpmtRestTemplate;
import com.mindware.backend.rest.user.UserRestTemplate;
import com.mindware.ui.util.UIUtils;
import com.mindware.ui.views.users.DialogUpdatePassword;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.VaadinSession;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@Route("")
@RouteAlias("login")
public class LoginView extends VerticalLayout {
    private final LoginI18n i18n = LoginI18n.createDefault();
    private boolean isHasPass;
    private String login;

    @Autowired
    private LoginRestTemplate restTemplate;

    @Autowired
    private UserRestTemplate userRestTemplate;

    @Autowired
    private GbpmtRestTemplate gbpmtRestTemplate;

    @Autowired
    private AdusrOfiRestTemplate adusrOfiRestTemplate;

//    protected void onAttach(AttachEvent attachEvent){
//        UI ui = getUI().get();
//        ui.setPollInterval(30000);
//    }


    public LoginView(){
        restTemplate = new LoginRestTemplate();
//        userRestTemplate = new UserRestTemplate();
        VaadinSession.getCurrent()
                .getSession()
                .setMaxInactiveInterval(30000);
        LoginForm component = new LoginForm();
        component.setI18n(createSpanishI18n());
        component.addLoginListener(e -> {

        });

        component.addLoginListener(e ->{
            JwtRequest jwtRequest = new JwtRequest();
            jwtRequest.setUsername(e.getUsername().toUpperCase());
            jwtRequest.setPassword(e.getPassword());
            try {
                Token token = restTemplate.getToken(jwtRequest);
                String loginUser = e.getUsername().toUpperCase();
//                VaadinSession.getCurrent().getSession().setMaxInactiveInterval(60000);
                VaadinSession.getCurrent().setAttribute("jwt", token.getToken());

                Users users = userRestTemplate.findByLogin(loginUser);
                AdusrOfi adusrOfi = adusrOfiRestTemplate.findByLogin(loginUser);
                Gbpmt gbpmt = gbpmtRestTemplate.findAll();
                VaadinSession.getCurrent().setAttribute("jwt", null);


                if(users.getState().equals("ACTIVO")) {
                    if(adusrOfi==null){
                        UIUtils.dialog("La cuenta de usuario no existe en el core financiero","alert").open();
                        return;
                    }
                    if(isCurrentPassword(users, gbpmt.getGbpmtfdia())==true){
                        VaadinSession.getCurrent().setAttribute("jwt", token.getToken());
                        VaadinSession.getCurrent().setAttribute("login",loginUser);
                        VaadinSession.getCurrent().setAttribute("type-change", gbpmt.getGbpmttcof());
                        VaadinSession.getCurrent().setAttribute("name-office",adusrOfi.getGbofides1());
                        VaadinSession.getCurrent().setAttribute("current-date",gbpmt.getGbpmtfdia());
                        VaadinSession.getCurrent().setAttribute("rol",users.getRolName());
                        VaadinSession.getCurrent().setAttribute("email",users.getEmail());
                        VaadinSession.getCurrent().setAttribute("plaza",adusrOfi.getAdusrplaz());

                        UI.getCurrent().navigate("main");
                    }else{
                        DialogUpdatePassword dialogUpdatePassword = new DialogUpdatePassword(userRestTemplate,users);
                        dialogUpdatePassword.open();
                    }

                }else if(users.getState().equals("RESET")){
                    DialogUpdatePassword dialogUpdatePassword = new DialogUpdatePassword(userRestTemplate,users);
                    dialogUpdatePassword.open();
                }
            }catch (Exception ex){
                component.setError(true);
            }
        });

        setSizeFull();
        getStyle().set("background","url(images/login-register.jpg");
        getStyle().set("align-center","stretch");
        setHorizontalComponentAlignment(Alignment.CENTER,component);
        add(component);
    }

    private Boolean isCurrentPassword(Users user, Date currentDate){
        if(user.getDateUpdatePassword()!=null){
            Date dateForReset = DateUtils.addDays(user.getDateUpdatePassword(),user.getNumDaysValidity()) ;
            if(dateForReset.after(currentDate) ){
                return true;
            } return false;
        }else {
            Date dateForReset = DateUtils.addDays(user.getCreateDate(),user.getNumDaysValidity()) ;
            if(dateForReset.after(currentDate) ){
                return true;
            } return false;
        }
    }

    private LoginI18n createSpanishI18n() {

        i18n.setHeader(new LoginI18n.Header());
        i18n.getHeader().setDescription("Automatizacion - Formularios");
        i18n.getForm().setUsername("Usuario");
        i18n.getForm().setTitle("Formularios");
        i18n.getForm().setSubmit("Entrar");
        i18n.getForm().setPassword("Clave");
        i18n.getForm().setForgotPassword("");
        i18n.getErrorMessage().setTitle("Usuario/clave invalida");
        i18n.getErrorMessage()
                .setMessage("Compruebe su usuario y contraseña y vuelva a intentarlo.");
        i18n.setAdditionalInformation("");
        return i18n;
    }


}
