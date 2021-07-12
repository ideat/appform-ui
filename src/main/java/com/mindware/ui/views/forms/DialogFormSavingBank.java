package com.mindware.ui.views.forms;

import com.mindware.backend.entity.Forms;
import com.mindware.backend.entity.netbank.dto.DataFormDto;
import com.mindware.backend.rest.forms.FormsRestTemplate;
import com.mindware.ui.util.UIUtils;
import com.mindware.ui.util.Util;
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
import com.vaadin.flow.theme.lumo.Lumo;

@CssImport("./styles/my-dialog.css")
public class DialogFormSavingBank extends Dialog {
    public String DOCK = "dock";
    public String FULLSCREEN = "fullscreen";

    private boolean isDocked = false;
    private boolean isFullScreen = false;

    private BeanValidationBinder<Forms> binder;

    private BeanValidationBinder<DataFormDto> binderDataFormDto;

    private Header header;
    private Button min;
    private Button max;
    private Button btnSave;
    private Button discardDraft;

    private VerticalLayout content;
    private Footer footer;

    private Forms forms;
    private FormsRestTemplate formsRestTemplateGlobal;



    public DialogFormSavingBank(String accountCode, String categoryTypeForm, String nameTypeForm, DataFormDto dataFormDto, FormsRestTemplate formsRestTemplate ){
        setDraggable(true);
        setModal(false);
        setResizable(true);
        formsRestTemplateGlobal = formsRestTemplate;
        // Dialog theming
        getElement().getThemeList().add("my-dialog");
        setWidth("800px");

        // Accessibility
        getElement().setAttribute("aria-labelledby", "dialog-title");

        // Header
        String textTitle = categoryTypeForm.equals("CAJA-AHORRO")?"Formulario Apertura de Ahorro -":"Formulario Apertura de DPF -";
        H2 title = new H2(textTitle + accountCode);
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

        btnSave = new Button("Guardar");
        discardDraft = new Button(VaadinIcon.TRASH.create());
        // Content
        forms = formsRestTemplateGlobal.findByIdAccountAndTypeFormAndCategoryTypeForm(accountCode,nameTypeForm,categoryTypeForm);
        binderDataFormDto = new BeanValidationBinder(DataFormDto.class);
        binder = new BeanValidationBinder<>(Forms.class);

        content = new VerticalLayout(layoutForm(dataFormDto,nameTypeForm,categoryTypeForm, accountCode));
        content.addClassName("dialog-content");
        content.setAlignItems(FlexComponent.Alignment.STRETCH);
        add(content);

        binderDataFormDto.readBean(dataFormDto);
        binder.readBean(forms);

        // Footer

        btnSave.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

//        Button attachFiles = new Button(VaadinIcon.PAPERCLIP.create());

        discardDraft.addThemeVariants(ButtonVariant.LUMO_ERROR,ButtonVariant.LUMO_TERTIARY);
        footer = new Footer(btnSave,  discardDraft);
        add(footer);

        // Button theming
        for (Button button : new Button[] { min, max, close }) {
            button.addThemeVariants(ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_TERTIARY);
        }
        maximise();
    }

    private Forms searchForms(String accountCode){
        Forms f = new Forms();


        return f;
    }

    private FormLayout layoutForm(DataFormDto dataFormDto, String nameTypeForm, String categoryTypeForm, String accountCode){
        TextField product = new TextField();
        product.setWidthFull();

        TextField currency = new TextField();
        currency.setWidthFull();

        TextField office = new TextField();
        office.setWidthFull();

        TextField openingDate = new TextField();
        openingDate.setWidthFull();

        TextField codeClient = new TextField();
        codeClient.setWidthFull();

        TextField lastName = new TextField();
        lastName.setReadOnly(true);
        lastName.setWidthFull();

        TextField motherLastName = new TextField();
        motherLastName.setWidthFull();

        TextField marriedLastName = new TextField();
        marriedLastName.setReadOnly(true);
        marriedLastName.setWidthFull();

        TextField names = new TextField();
        names.setReadOnly(true);
        names.setWidthFull();

        TextField typeIdentificationCard = new TextField();
        typeIdentificationCard.setReadOnly(true);
        typeIdentificationCard.setWidthFull();

        TextField numberIdentificationCard = new TextField();
        numberIdentificationCard.setReadOnly(true);
        numberIdentificationCard.setWidthFull();

        TextField gender = new TextField();
        gender.setReadOnly(true);
        gender.setWidthFull();

        TextField civilStatus = new TextField();

        civilStatus.setReadOnly(true);
        civilStatus.setWidthFull();


        TextField expirationDateIdCard = new TextField();
        expirationDateIdCard.setWidthFull();

        TextField spouseName = new TextField();
        spouseName.setWidthFull();

        TextField bornDate = new TextField();
        bornDate.setWidthFull();

        TextField country = new TextField();
        country.setWidthFull();

        TextField economicActivitySpouse = new TextField();
        economicActivitySpouse.setWidthFull();

        TextField departament = new TextField();
        departament.setWidthFull();

        TextField province = new TextField();
        province.setWidthFull();

        TextField homeAddress = new TextField();
        homeAddress.setWidthFull();

        TextField profession = new TextField();
        profession.setWidthFull();

        NumberField mountlyIncome = new NumberField();
        mountlyIncome.setWidthFull();

        TextField zone = new TextField();
        zone.setWidthFull();

        TextField city = new TextField();
        city.setWidthFull();

        TextField economicActivity = new TextField();
        economicActivity.setWidthFull();

        TextField homePhone = new TextField();
        homePhone.setWidthFull();

        TextField cellphone = new TextField();
        cellphone.setWidthFull();

        TextField secondEconomicActivity = new TextField();;
        secondEconomicActivity.setWidthFull();

        TextField reasonOpeningAccount = new TextField();
        reasonOpeningAccount.setWidthFull();

        //Data for save
        ComboBox<String> linkingAccount = new ComboBox<>();
        linkingAccount.setItems("TITULAR","COTITULAR", "PADRES O TUTORES LEGALES", "APODERADOS", "REPRESENTANTE LEGAL");
        linkingAccount.setWidthFull();

        RadioButtonGroup<String> isFinalBenifeciary = new RadioButtonGroup<>();
        isFinalBenifeciary.setItems("SI","NO");


        FormLayout formAccount = new FormLayout();
        formAccount.setSizeUndefined();
        formAccount.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("600px", 2,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("800px", 3,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("1024px", 4,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP)
        );

        ////////////////////////
        binderDataFormDto.forField(product).bind(DataFormDto::getProduct,DataFormDto::setProduct);
        binderDataFormDto.forField(currency).bind(DataFormDto::getCurrency,DataFormDto::setCurrency);
        binderDataFormDto.forField(office).bind(DataFormDto::getOfficeName,DataFormDto::setOfficeName);
        binderDataFormDto.forField(openingDate).withConverter(new Util.DateToString()).bind(DataFormDto::getOpeningDate,DataFormDto::setOpeningDate);
        binderDataFormDto.forField(codeClient).withConverter(new Util.IntegerToString()).bind(DataFormDto::getCodeClient,DataFormDto::setCodeClient);
        binderDataFormDto.forField(lastName).bind(DataFormDto::getLastName,DataFormDto::setLastName);
        binderDataFormDto.forField(motherLastName).bind(DataFormDto::getMotherLastName,DataFormDto::setMotherLastName);
        binderDataFormDto.forField(marriedLastName).bind(DataFormDto::getMarriedLastName,DataFormDto::setMarriedLastName);
        binderDataFormDto.forField(names).bind(DataFormDto::getNames,DataFormDto::setNames);
        binderDataFormDto.forField(typeIdentificationCard).bind(DataFormDto::getTypeDocument,DataFormDto::setTypeDocument);
        binderDataFormDto.forField(numberIdentificationCard).bind(DataFormDto::getIdCard,DataFormDto::setIdCard);
        binderDataFormDto.forField(gender).bind(DataFormDto::getGender,DataFormDto::setGender);
        binderDataFormDto.forField(civilStatus).bind(DataFormDto::getCivilStatus,DataFormDto::setCivilStatus);
        binderDataFormDto.forField(expirationDateIdCard).withConverter(new Util.DateToString()).bind(DataFormDto::getExpiredDate,DataFormDto::setExpiredDate);
        binderDataFormDto.forField(spouseName).bind(DataFormDto::getFullNameSpouse,DataFormDto::setFullNameSpouse);
        binderDataFormDto.forField(bornDate).withConverter(new Util.DateToString()).bind(DataFormDto::getBornDate,DataFormDto::setBornDate);
        binderDataFormDto.forField(country).bind(DataFormDto::getCountry,DataFormDto::setCountry);
        binderDataFormDto.forField(economicActivitySpouse).bind(DataFormDto::getActivitySpouse,DataFormDto::setActivitySpouse);
        binderDataFormDto.forField(departament).bind(DataFormDto::getDepartament,DataFormDto::setDepartament);
        binderDataFormDto.forField(province).bind(DataFormDto::getProvince,DataFormDto::setProvince);
        binderDataFormDto.forField(homeAddress).bind(DataFormDto::getAddressHome,DataFormDto::setAddressHome);
        binderDataFormDto.forField(profession).bind(DataFormDto::getProfession,DataFormDto::setProfession);
        binderDataFormDto.forField(mountlyIncome).bind(DataFormDto::getIncomeMountly,DataFormDto::setIncomeMountly);
        binderDataFormDto.forField(zone).bind(DataFormDto::getZone,DataFormDto::setZone);
        binderDataFormDto.forField(city).bind(DataFormDto::getCity,DataFormDto::setCity);
        binderDataFormDto.forField(homePhone).bind(DataFormDto::getHomePhone,DataFormDto::setHomePhone);
        binderDataFormDto.forField(cellphone).bind(DataFormDto::getCellphone,DataFormDto::setCellphone);
        binderDataFormDto.forField(economicActivity).bind(DataFormDto::getActivity1,DataFormDto::setActivity1);
        binderDataFormDto.forField(secondEconomicActivity).bind(DataFormDto::getActivity2,DataFormDto::setActivity2);
        binderDataFormDto.forField(reasonOpeningAccount).bind(DataFormDto::getReasonOpeningAccount,DataFormDto::setReasonOpeningAccount);

        binderDataFormDto.setReadOnly(true);
        //////////


        binder.forField(linkingAccount)
                .asRequired("Seleccione Vinculacion a la Cuenta")
                .bind(Forms::getLinkingAccount,Forms::setLinkingAccount);
        binder.forField(isFinalBenifeciary)
                .asRequired("Se debe indicar si es el beneficiario final")
                .bind(Forms::getIsFinalBeneficiary,Forms::setIsFinalBeneficiary);

        formAccount.addFormItem(product,"Producto");
        formAccount.addFormItem(currency,"Moneda");
        formAccount.addFormItem(office,"Oficina");
        formAccount.addFormItem(openingDate,"Fecha apertura");
        formAccount.addFormItem(codeClient,"Código de cliente");
        formAccount.addFormItem(lastName,"Primer apellido");
        formAccount.addFormItem(motherLastName,"Segundo Apellido");
        formAccount.addFormItem(marriedLastName,"Apellido de Casada");
        formAccount.addFormItem(names, "Nombres");
        formAccount.addFormItem(typeIdentificationCard,"Tipo de identificación");
        formAccount.addFormItem(numberIdentificationCard,"Número de documento");
        formAccount.addFormItem(expirationDateIdCard,"Fecha de vencimiento");
        formAccount.addFormItem(bornDate,"Fecha de nacimiento");
        formAccount.addFormItem(country,"Pais");
        formAccount.addFormItem(departament,"Departamento");
        formAccount.addFormItem(province,"Provincia");
        formAccount.addFormItem(homeAddress,"Dirección Domicilio");
        formAccount.addFormItem(zone,"Zona");
        formAccount.addFormItem(city,"Ciudad");
        formAccount.addFormItem(homePhone,"Teléfono Domicilio");
        formAccount.addFormItem(cellphone,"Teléfono Móvil");

        formAccount.addFormItem(spouseName,"Nombre del Conyuge");
        formAccount.addFormItem(economicActivitySpouse,"Activad Economica u Oficio Princial del Conyuge");
        formAccount.addFormItem(profession,"Profesion");
        formAccount.addFormItem(mountlyIncome,"Ingresos Mensuales/Bs");
        formAccount.addFormItem(economicActivity,"Actividad Económica");
        formAccount.addFormItem(secondEconomicActivity,"2da Actividad Económica");

        formAccount.addFormItem(reasonOpeningAccount,"Motivo de la apertura de la Cuenta");
        formAccount.addFormItem(linkingAccount,"Vinculacion a la Cuenta");

        formAccount.addFormItem(isFinalBenifeciary,"Usted es el Beneficiario Final?");

        Button btnBeneficiary = new Button("Beneficiarios");
        btnBeneficiary.setEnabled(false);

        btnBeneficiary.addClickListener(click -> {
            BeneficiaryView beneficiaryView = new BeneficiaryView("[]");
            Footer footer = new Footer();
            Button save = new Button("Añadir al formulario");
            save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            footer.add(save);
            save.addClickListener(event -> {
                forms.setBeneficiary(beneficiaryView.getFinalBeneficiaryList());
            });

            beneficiaryView.open();
            beneficiaryView.add(footer);
        });

        //Save changes
        btnSave.addClickListener(event -> {
           if(binder.writeBeanIfValid(forms)){
               forms.setNameTypeForm(nameTypeForm);
               forms.setCategoryTypeForm(categoryTypeForm);
               forms.setIdAccount(accountCode);
               forms.setIdClient(dataFormDto.getCodeClient());
               formsRestTemplateGlobal.create(forms);
               UIUtils.dialog("Datos formulario registrados","success").open();
               close();
           }else{
               UIUtils.dialog("Error, verfique los datos","alert").open();
           }
        });

        isFinalBenifeciary.addValueChangeListener(event -> {
            if(event.getValue().equals("SI")) btnBeneficiary.setEnabled(false);
            if(event.getValue().equals("NO")) btnBeneficiary.setEnabled(true);
        });

        formAccount.addFormItem(btnBeneficiary,"");

        return formAccount;
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
