package com.mindware.ui.views.users;

import com.mindware.backend.entity.Users;
import com.mindware.backend.rest.email.MailRestTemplate;
import com.mindware.backend.rest.user.UserRestTemplate;
import com.mindware.backend.util.GrantOptions;
import com.mindware.backend.util.PrepareMail;
import com.mindware.ui.components.detailsdrawer.DetailsDrawerFooter;
import com.mindware.ui.util.UIUtils;
import com.mindware.ui.util.Util;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.Lumo;

import java.util.Date;
import java.util.Optional;

@CssImport("./styles/my-dialog.css")
public class DialogUserCreate extends Dialog {
    public String DOCK = "dock";
    public String FULLSCREEN = "fullscreen";

    private boolean isDocked = false;
    private boolean isFullScreen = false;

    private Header header;
    private Button min;
    private Button max;

    private VerticalLayout content;
    public Footer footer;

    private BeanValidationBinder<Users> binder;
    private UserRestTemplate userRestTemplate;
    private Users userGlobal;
    private PrepareMail prepareMailGlobal;
    public TextField login;

    public DialogUserCreate(UserRestTemplate restTemplate, Users user, PrepareMail prepareMail){

        setDraggable(true);
        setModal(false);
        setResizable(true);
        userRestTemplate = restTemplate;
        userGlobal = user;
        prepareMailGlobal = prepareMail;

        // Dialog theming
        getElement().getThemeList().add("my-dialog");
        setWidth("800px");

        // Accessibility
        getElement().setAttribute("aria-labelledby", "dialog-title");

        // Header
        String textTitle="REGISTRO USUARIOS";
        H2 title = new H2(textTitle );
        title.addClassName("dialog-title");

        min = new Button(VaadinIcon.DOWNLOAD_ALT.create());
        min.addClickListener(event -> minimise());

        max = new Button(VaadinIcon.EXPAND_SQUARE.create());
        max.addClickListener(event -> maximise());


        Button close = new Button(VaadinIcon.CLOSE_SMALL.create());
        close.addClickListener(event -> close());

        header = new Header(title, min, max, close);
        header.getElement().getThemeList().add(Lumo.DARK);
        add(header);


        //Content


        content = new VerticalLayout(createFormLayout());
        content.addClassName("dialog-content");
        content.setAlignItems(FlexComponent.Alignment.STRETCH);
        add(content);

        //Footer


        footer = new Footer();
        add(footer);

        // Button theming
        for (Button button : new Button[] { min, max, close }) {
            button.addThemeVariants(ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_TERTIARY);
        }
    }

    public Users saveUser(){
        if(binder.writeBeanIfValid(userGlobal)){
            if(userGlobal.getId()==null){
                userGlobal.setPassword(Util.generateRandomPassword());
                Date createdate = (Date) VaadinSession.getCurrent().getAttribute("current-date");
                userGlobal.setCreateDate(createdate);
                userGlobal.setState("RESET");
                userRestTemplate.add(userGlobal);
                prepareMailGlobal.sendMailCreateUser(userGlobal,userGlobal.getPassword()
                        , userGlobal.getLogin()
                        , VaadinSession.getCurrent().getAttribute("email").toString());
            }else{
                userRestTemplate.update(userGlobal);
            }
            UIUtils.dialog("Usuario registrado","success").open();
            close();
            return userGlobal;
        }else{
            UIUtils.dialog("Error al crear, revise los datos","alert").open();
            return null;
        }

    }

    private FormLayout createFormLayout(){

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
        state.setValue(Optional.ofNullable(userGlobal.getState()).orElse("").equals("ACTIVO") ? "ACTIVO":"BAJA");

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
//        btnReset.setEnabled(GrantOptions.grantedOption("Usuarios") && user.getId()!=null);
        btnReset.addClickListener(event -> {
            userGlobal.setState("RESET");
            userGlobal.setPassword(Util.generateRandomPassword());
            userRestTemplate.updatePassword(userGlobal);
//            userRestTemplate.update(userGlobal);
            UIUtils.dialog("Operacion realizada correctamente, se envio un correo al usuario","success").open();
            prepareMailGlobal.sendMailResetPassword(userGlobal,userGlobal.getPassword()
                    , VaadinSession.getCurrent().getAttribute("login").toString()
                    , VaadinSession.getCurrent().getAttribute("email").toString());
            close();
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

        binder.readBean(userGlobal);

        return formLayout;
    }

    private void minimise() {
        if (isDocked) {
            initialSize();
        } else {
            if (isFullScreen) {
                initialSize();
            }
            min.setIcon(VaadinIcon.UPLOAD_ALT.create());
            getElement().getThemeList().add(DOCK);
            setWidth("620px");
        }
        isDocked = !isDocked;
        isFullScreen = false;
        content.setVisible(!isDocked);
        footer.setVisible(!isDocked);
    }

    private void initialSize() {
        min.setIcon(VaadinIcon.DOWNLOAD_ALT.create());
        getElement().getThemeList().remove(DOCK);
        max.setIcon(VaadinIcon.EXPAND_SQUARE.create());
        getElement().getThemeList().remove(FULLSCREEN);
        setHeight("auto");
        setWidth("600px");
    }

    private void maximise() {
        if (isFullScreen) {
            initialSize();
        } else {
            if (isDocked) {
                initialSize();
            }
            max.setIcon(VaadinIcon.COMPRESS_SQUARE.create());
            getElement().getThemeList().add(FULLSCREEN);
            setSizeFull();
            content.setVisible(true);
            footer.setVisible(true);
        }
        isFullScreen = !isFullScreen;
        isDocked = false;
    }
}
