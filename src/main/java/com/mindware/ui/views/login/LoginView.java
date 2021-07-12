package com.mindware.ui.views.login;

import com.mindware.backend.entity.Users;
import com.mindware.backend.rest.login.JwtRequest;
import com.mindware.backend.rest.login.LoginRestTemplate;
import com.mindware.backend.rest.login.Token;
import com.mindware.backend.rest.user.UserRestTemplate;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Route("")
@RouteAlias("login")
public class LoginView extends VerticalLayout {
    private final LoginI18n i18n = LoginI18n.createDefault();
    private boolean isHasPass;
    private String login;
    private LoginRestTemplate restTemplate;

    @Autowired
    private UserRestTemplate userRestTemplate;

    public LoginView(){
        restTemplate = new LoginRestTemplate();
//        userRestTemplate = new UserRestTemplate();
        LoginForm component = new LoginForm();
        component.setI18n(createSpanishI18n());

        component.addLoginListener(e ->{
            JwtRequest jwtRequest = new JwtRequest();
            jwtRequest.setUsername(e.getUsername());
            jwtRequest.setPassword(e.getPassword());
            try {
                Token token = restTemplate.getToken(jwtRequest);
                VaadinSession.getCurrent().setAttribute("jwt", token.getToken());
                VaadinSession.getCurrent().setAttribute("login",e.getUsername());
                Users users = userRestTemplate.findByLogin(e.getUsername());
                if(users.getState().equals("active")) {
                    UI.getCurrent().navigate("main");
                }else if(users.getState().equals("RESET")){
                    Map<String, List<String>> param = new HashMap<>();
                    List<String> login = new ArrayList<>();
                    login.add(e.getUsername());
                    param.put("login",login);
                    QueryParameters qp = new QueryParameters(param);
                    UI.getCurrent().navigate("user-update-password",qp);
                }
            }catch (Exception ex){
                component.setError(true);
            }
        });

        setSizeFull();
        getStyle().set("background","url(images/background-login.jpg");
        getStyle().set("align-center","stretch");
        setHorizontalComponentAlignment(Alignment.CENTER,component);
        add(component);
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
