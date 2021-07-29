package com.mindware.ui.views.users;

import com.mindware.backend.entity.Users;
import com.mindware.backend.rest.user.UserRestTemplate;
import com.mindware.ui.util.UIUtils;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.Lumo;

import java.util.Date;

@CssImport("./styles/my-dialog.css")
public class DialogUpdatePassword extends Dialog {

    public String DOCK = "dock";
    public String FULLSCREEN = "fullscreen";

    private boolean isDocked = false;
    private boolean isFullScreen = false;

    private Header header;
    private Button min;
    private Button max;

    private VerticalLayout content;
    public Footer footer;

    private Users userGlobal;
    private UserRestTemplate restTemplate;

    public DialogUpdatePassword(UserRestTemplate userRestTemplate, Users users){

        setDraggable(true);
        setModal(false);
        setResizable(true);

        userGlobal = users;
        restTemplate = userRestTemplate;
        // Dialog theming
        getElement().getThemeList().add("my-dialog");
        setWidth("800px");

        // Accessibility
        getElement().setAttribute("aria-labelledby", "dialog-title");

        // Header
        String textTitle="ACTUALIZAR CLAVE USUARIO";
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

        content = new VerticalLayout(updateLayout());
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

    private VerticalLayout updateLayout(){
//        PasswordField oldPassword = new PasswordField("Clave anterior");
//        oldPassword.setWidth("100%");
//        oldPassword.setRequired(true);
//        oldPassword.setRequiredIndicatorVisible(true);

        PasswordField newPassword = new PasswordField("Nueva clave");
        newPassword.setWidth("100%");
        newPassword.setRequired(true);
        newPassword.setRequiredIndicatorVisible(true);

        PasswordField confirmNewPassword = new PasswordField("Confirme nueva clave");
        confirmNewPassword.setWidth("100%");
        confirmNewPassword.setRequired(true);
        confirmNewPassword.setRequiredIndicatorVisible(true);

        VerticalLayout layout = new VerticalLayout();

        Button btnUpdate = new Button("Actualizar");
        btnUpdate.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        btnUpdate.addClickListener(click ->{
           if(newPassword.getValue()==null || newPassword.getValue().isEmpty()){
               UIUtils.dialog("Ingrese la nueva clave","alert").open();;
               return;
           }
           if(validatePassword(newPassword.getValue(), confirmNewPassword.getValue())){
               userGlobal.setPassword(newPassword.getValue());
               Date currentDate = (Date) VaadinSession.getCurrent().getAttribute("current-date");
               userGlobal.setDateUpdatePassword(currentDate);
               userGlobal.setState("ACTIVO");
               restTemplate.updatePassword(userGlobal);
               UIUtils.dialog("Clave actualizada","success").open();
               VaadinSession.getCurrent().setAttribute("jwt", null);
               close();
               UI.getCurrent().getPage().reload();
           }else{

               UIUtils.dialog("Clave diferente, vuelva a ingresar","info").open();
               return;
           }
        });

        layout.add(newPassword,confirmNewPassword,btnUpdate);

        return layout;
    }

    private Boolean validatePassword(String newPassword, String confirmNewPassword){
        if(newPassword.equals(confirmNewPassword)){
            return true;
        }
        return false;

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
