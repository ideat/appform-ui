package com.mindware.ui.views.forms;

import com.mindware.backend.entity.Beneficiary;
import com.mindware.ui.components.FlexBoxLayout;
import com.mindware.ui.layout.size.Right;
import com.mindware.ui.util.UIUtils;
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
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.theme.lumo.Lumo;

import java.util.UUID;

@CssImport("./styles/my-dialog.css")
public class BeneficiaryRegisterView extends Dialog {
    public String DOCK = "dock";
    public String FULLSCREEN = "fullscreen";

    private boolean isDocked = false;
    private boolean isFullScreen = false;

    private Header header;
    private Button min;
    private Button max;
    private BeanValidationBinder<Beneficiary> binder;

    private VerticalLayout content;
//    private Footer footer;
//    public String valueClose = "salio";
    public Beneficiary beneficiaryGlobal;
    public BeneficiaryRegisterView(Beneficiary beneficiary){
        beneficiaryGlobal = beneficiary;
        setDraggable(true);
        setModal(false);
        setResizable(true);

        // Dialog theming
        getElement().getThemeList().add("my-dialog");
        setWidth("800px");

        // Accessibility
        getElement().setAttribute("aria-labelledby", "dialog-title");

        // Header
        H2 title = new H2("Formulario Registro Beneficiario" );
        title.addClassName("dialog-title");

        min = new Button(VaadinIcon.DOWNLOAD_ALT.create());
        min.addClickListener(event -> minimise());

        max = new Button(VaadinIcon.EXPAND_SQUARE.create());
        max.addClickListener(event -> maximise());

        //////////////////

        Button close = new Button(VaadinIcon.CLOSE_SMALL.create());
        close.addClickListener(event -> close());

        header = new Header(title, min, max, close);
        header.getElement().getThemeList().add(Lumo.DARK);
        add(header);

        // Content

        content = new VerticalLayout(registerForm());
        content.addClassName("dialog-content");
        content.setAlignItems(FlexComponent.Alignment.STRETCH);
        add(content);

        Button attachFiles = new Button(VaadinIcon.PAPERCLIP.create());
        Button discardDraft = new Button(VaadinIcon.TRASH.create());

        // Button theming
        for (Button button : new Button[] { min, max, close, attachFiles, discardDraft }) {
            button.addThemeVariants(ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_TERTIARY);
        }

    }

    public boolean save(){
        boolean result = false;
       if(binder.writeBeanIfValid(beneficiaryGlobal)){
           if(beneficiaryGlobal.getId()==null){
               beneficiaryGlobal.setId(UUID.randomUUID().toString());
           }
           result = true;
       }
       return result;
    }

    public FormLayout registerForm(){

        binder = new BeanValidationBinder<>(Beneficiary.class);


        TextField fullName = new TextField();
        fullName.setWidthFull();

        TextField idCard = new TextField();
        idCard.setWidth("100%");

        TextField telephone  = new TextField();
        telephone.setWidthFull();

        TextField economicActivity = new TextField();
        economicActivity.setWidthFull();

        ComboBox<String> nationality = new ComboBox();
        nationality.setItems("BOLIVIANA","ARGENTINA" );
        nationality.setWidthFull();

        TextField address = new TextField();
        address.setWidthFull();

        TextField sourceFounds = new TextField();
        sourceFounds.setWidthFull();

        FormLayout formBeneficiary = new FormLayout();
        formBeneficiary.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("600px", 2,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("800px", 3,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("1024px", 4,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP)
        );

        binder.forField(fullName).asRequired("Nombre completo es requerido")
                .bind(Beneficiary::getFullName,Beneficiary::setFullName);
        binder.forField(idCard).asRequired("Nro documento es requerido")
                .bind(Beneficiary::getIdCard,Beneficiary::setIdCard);
        binder.forField(telephone).bind(Beneficiary::getTelephone,Beneficiary::setTelephone);
        binder.forField(economicActivity).bind(Beneficiary::getEconomicActivity,Beneficiary::setEconomicActivity);
        binder.forField(nationality).bind(Beneficiary::getNationality,Beneficiary::setNationality);
        binder.forField(address).bind(Beneficiary::getAddress,Beneficiary::setAddress);
        binder.forField(sourceFounds).bind(Beneficiary::getSourceFounds,Beneficiary::setSourceFounds);

        formBeneficiary.addFormItem(fullName,"Nombre completo");
        formBeneficiary.addFormItem(idCard,"Documento identificacion");
        formBeneficiary.addFormItem(telephone,"Telefono");
        formBeneficiary.addFormItem(nationality,"Nacionalidad");
        formBeneficiary.addFormItem(economicActivity,"Actividad economica");
        formBeneficiary.addFormItem(address,"Direccion");
        formBeneficiary.addFormItem(sourceFounds,"Origen de los fondos");

        binder.readBean(beneficiaryGlobal);

        return formBeneficiary;

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
//        footer.setVisible(!isDocked);
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
//            footer.setVisible(true);
        }
        isFullScreen = !isFullScreen;
        isDocked = false;
    }



}
