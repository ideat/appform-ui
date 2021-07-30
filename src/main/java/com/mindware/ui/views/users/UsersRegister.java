package com.mindware.ui.views.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindware.backend.entity.Users;
import com.mindware.backend.rest.user.UserRestTemplate;
import com.mindware.backend.util.GrantOptions;
import com.mindware.backend.util.PrepareMail;
import com.mindware.ui.MainLayout;
import com.mindware.ui.components.FlexBoxLayout;
import com.mindware.ui.components.detailsdrawer.DetailsDrawer;
import com.mindware.ui.components.detailsdrawer.DetailsDrawerFooter;
import com.mindware.ui.components.detailsdrawer.DetailsDrawerHeader;
import com.mindware.ui.components.navigation.bar.AppBar;
import com.mindware.ui.layout.size.Horizontal;
import com.mindware.ui.util.UIUtils;
import com.mindware.ui.util.Util;
import com.mindware.ui.views.SplitViewFrame;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Route(value = "user-register", layout = MainLayout.class)
@PageTitle("Registro de Usuario")
public class UsersRegister extends SplitViewFrame implements HasUrlParameter<String> {

    private BeanValidationBinder<Users> binder;
    private ObjectMapper mapper = new ObjectMapper();
    private Users users;
    @Autowired
    private UserRestTemplate restTemplate;

    @Autowired
    private PrepareMail prepareMail;

    private DetailsDrawer detailsDrawer;
    private DetailsDrawerHeader detailsDrawerHeader;
    private DetailsDrawerFooter footer;

    private NumberField codeOffice;

    private TextField login;

    @Override
    public void setParameter(BeforeEvent beforeEvent, String s) {


        if(s.contains("NUEVO")){
            users = new Users();
            users.setPassword("password");
            setViewContent(createContent());

        }else{
            users = restTemplate.findByLogin(s);
            setViewContent(createContent());
            login.setEnabled(false);

        }
        setViewDetails(createDetailDrawer());
        setViewDetailsPosition(Position.BOTTOM);
        binder.readBean(users);

    }

    @Override
    protected void onAttach(AttachEvent attachEvent){
        super.onAttach(attachEvent);
        AppBar appBar = initAppBar();
        appBar.setTitle(users.getFullName().equals("null null")?"Nuevo":users.getFullName());

    }

    private AppBar initAppBar(){
        AppBar appBar = MainLayout.get().getAppBar();
        appBar.setNaviMode(AppBar.NaviMode.CONTEXTUAL);
        appBar.getContextIcon().addClickListener(e ->{
            UI.getCurrent().navigate(UserView.class);
        });

        return appBar;
    }

    private DetailsDrawer createDetailDrawer(){
        detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.BOTTOM);

        // Header
        detailsDrawerHeader = new DetailsDrawerHeader("");
        detailsDrawerHeader.addCloseListener(buttonClickEvent -> detailsDrawer.hide());
        detailsDrawer.setHeader(detailsDrawerHeader);

        return detailsDrawer;
    }

    private Component createContent(){

        FlexBoxLayout content = new FlexBoxLayout(createUser(users));
        content.setFlexDirection(FlexLayout.FlexDirection.COLUMN);
        content.setMargin(Horizontal.AUTO, Horizontal.RESPONSIVE_L);

        return content;
    }

    private DetailsDrawer createUser(Users users){

        login = new TextField();
        login.setWidth("100%");
        login.setRequired(true);
        login.setRequiredIndicatorVisible(true);


        TextField names = new TextField();
        names.setWidth("100%");
        names.setRequired(true);
        names.setRequiredIndicatorVisible(true);

        TextField lastNames = new TextField();
        lastNames.setWidth("100%");
        lastNames.setRequired(true);
        lastNames.setRequiredIndicatorVisible(true);

        RadioButtonGroup<String> state = new RadioButtonGroup<>();
        state.setItems("ACTIVO","BAJA");
        state.setValue(Optional.ofNullable(users.getState()).orElse("").equals("ACTIVO") ? "ACTIVO":"BAJA");



        ComboBox<String> rols = new ComboBox<>();
        rols.setItems("ADMINISTRADOR","USUARIO");
        rols.setRequired(true);
        rols.setRequiredIndicatorVisible(true);

        NumberField numDaysValidity = new NumberField();
        numDaysValidity.setWidth("100%");
        numDaysValidity.setRequiredIndicatorVisible(true);

        TextField email = new TextField();
        email.setWidth("100%");
        email.setRequired(true);
        email.setRequiredIndicatorVisible(true);


        Button btnReset = new Button("Reset Password");
        btnReset.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_CONTRAST);
        btnReset.setWidth("100%");
        btnReset.setEnabled(GrantOptions.grantedOption("Usuarios") && users.getId()!=null);
        btnReset.addClickListener(event -> {
           users.setState("RESET");
           users.setPassword(Util.generateRandomPassword());
//           restTemplate.updateUser(users);
           restTemplate.updatePassword(users);
           UIUtils.dialog("Operacion realizada correctamente, se envio un correo la usuario","success").open();
           prepareMail.sendMailResetPassword(users,users.getPassword()
                   , VaadinSession.getCurrent().getAttribute("login").toString()
                   , VaadinSession.getCurrent().getAttribute("email").toString());
        });

        binder = new BeanValidationBinder<>(Users.class);
        binder.forField(login).asRequired("Login es requerido").bind(Users::getLogin,Users::setLogin);
        binder.forField(names).asRequired("Nombre es requerido").bind(Users::getFullName,Users::setFullName);
        binder.forField(state).asRequired("Estado es requerido").bind(Users::getState,Users::setState);
        binder.forField(rols).asRequired("Rol es requerido").bind(Users::getRolName,Users::setRolName);
        binder.forField(numDaysValidity).asRequired("Dias de Validez es requerido")
                .withConverter(new Util.DoubleToIntegerConverter())
                .bind(Users::getNumDaysValidity,Users::setNumDaysValidity);
        binder.forField(email).asRequired("Email es requerido")
                .withValidator(new EmailValidator("Correo invalido"))
                .bind(Users::getEmail,Users::setEmail);

        binder.addStatusChangeListener(event ->{
            boolean isValid = !event.hasValidationErrors();
            boolean hasChanges = binder.hasChanges() ;
            footer.saveState(isValid && hasChanges && GrantOptions.grantedOption("Usuarios"));
        });
        // Form layout
        FormLayout formLayout = new FormLayout();
        formLayout.setSizeUndefined();
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("600px", 2,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("800px", 3,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP)
        );

        formLayout.addFormItem(login,"Login");
        formLayout.addFormItem(names,"Nombres");
        formLayout.addFormItem(state,"Estado Usuario");
        formLayout.addFormItem(rols,"Rol");
        formLayout.addFormItem(numDaysValidity,"Dias validez password");
        formLayout.addFormItem(email,"Correo electronico");
        formLayout.addFormItem(btnReset,"");

        footer = new DetailsDrawerFooter();
        footer.addSaveListener(e ->{
            if(binder.writeBeanIfValid(users)){
                users.setPassword(Util.generateRandomPassword());
                try {
                    restTemplate.add(users);
                    UIUtils.dialog("Usuario Registrado","success").open();
                    if(users.getId()==null){
                        prepareMail.sendMailCreateUser(users, users.getPassword()
                                , VaadinSession.getCurrent().getAttribute("login").toString()
                                , VaadinSession.getCurrent().getAttribute("email").toString());
                    }
                    UI.getCurrent().navigate(UserView.class);
                }catch (Exception ex){

                    UIUtils.dialog("Error al guardar,  Show Error Details detalla el Error","alert").open();
                }
            }
        });
        footer.addCancelListener(e ->{

        });

        DetailsDrawer detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.BOTTOM);
        detailsDrawer.setHeight("90%");
        detailsDrawer.setWidth("100%");
        detailsDrawer.setContent(formLayout);
        detailsDrawer.setFooter(footer);

        return detailsDrawer;
    }

//    private void showSearch(){
//
//        detailsDrawerHeader.setTitle("Seleccionar Oficina");
//        detailsDrawer.setContent(searchOffice());
//        detailsDrawer.show();
//    }






}
