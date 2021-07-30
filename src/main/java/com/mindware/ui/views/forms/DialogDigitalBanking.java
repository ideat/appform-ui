package com.mindware.ui.views.forms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindware.backend.entity.AccountServiceOperation;
import com.mindware.backend.entity.Forms;
import com.mindware.backend.entity.Parameter;
import com.mindware.backend.entity.Service;
import com.mindware.backend.entity.netbank.dto.DataFormDto;
import com.mindware.backend.rest.forms.FormsRestTemplate;
import com.mindware.ui.util.UIUtils;
import com.mindware.ui.util.Util;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.Lumo;

import java.time.format.DateTimeFormatter;
import java.util.*;

@CssImport("./styles/my-dialog.css")
public class DialogDigitalBanking extends Dialog {

    public String DOCK = "dock";
    public String FULLSCREEN = "fullscreen";

    private boolean isDocked = false;
    private boolean isFullScreen = false;

    private Header header;
    private Button min;
    private Button max;
    private Button btnClose;
//    private Button discardDraft;

    private VerticalLayout content;
    private Footer footer;

    private List<Service> servicesList;
    private List<Service> operationsList;

    private List<AccountServiceOperation> accountServiceOperationList;
    private List<DataFormDto> dataFormDtoGlobal;

    private BeanValidationBinder<Forms> binderForms;
    private BeanValidationBinder<DataFormDto> binderDataFormDto;
    private List<Parameter> parameterListGlobal;
    private Forms formsDigitalBank;

    private DialogServiceOperationDigitalBank dialogServiceOperationDigitalBank;

    private  Set<String> accountsSelected;
    private Grid<AccountServiceOperation> grid;

    private FormsRestTemplate formsRestTemplateGlobal;
    private DataFormDto dataFormDto;

    public DialogDigitalBanking(List<DataFormDto> dataFormDto, List<Parameter> parameterList, FormsRestTemplate formsRestTemplate)  {

        setDraggable(true);
        setModal(false);
        setResizable(true);

//        servicesList = new ArrayList<>();
        parameterListGlobal = parameterList;
        dataFormDtoGlobal = dataFormDto;
        formsDigitalBank = formsRestTemplate.findByIdClientAndTypeFormAndCategoryTypeForm(dataFormDto.get(0).getCodeClient(),"BANCA DIGITAL","VARIOS");

        formsRestTemplateGlobal = formsRestTemplate;

        // Dialog theming
        getElement().getThemeList().add("my-dialog");
        setWidth("800px");

        // Accessibility
        getElement().setAttribute("aria-labelledby", "dialog-title");

        // Header
        String textTitle="BANCA DIGITAL";
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

        btnClose = new Button("Cerrar");
//        discardDraft = new Button(VaadinIcon.TRASH.create());
        // Content

        binderDataFormDto = new BeanValidationBinder(DataFormDto.class);
        binderForms = new BeanValidationBinder<>(Forms.class);
        createGridExistingForms();
        content = new VerticalLayout(formDataClient(),layoutAccounts(),grid );
        content.addClassName("dialog-content");
        content.setAlignItems(FlexComponent.Alignment.STRETCH);
        add(content);

//        binderDataFormDto.readBean(dataFormDto);
//        binder.readBean(forms);

        // Footer

        btnClose.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_CONTRAST);

//        Button attachFiles = new Button(VaadinIcon.PAPERCLIP.create());

//        discardDraft.addThemeVariants(ButtonVariant.LUMO_ERROR,ButtonVariant.LUMO_TERTIARY);
        footer = new Footer(btnClose);
        add(footer);

        // Button theming
        for (Button button : new Button[] { min, max, close }) {
            button.addThemeVariants(ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_TERTIARY);
        }
        maximise();

        btnClose.addClickListener(event -> {
            close();
//           if(binderForms.writeBeanIfValid(formsDigitalBank)){
//               formsDigitalBank.setCategoryTypeForm("VARIOS");
//               formsDigitalBank.setNameTypeForm("BANCA DIGITAL");
//               formsDigitalBank.setIdClient(dataFormDto.get(0).getCodeClient());
//               formsDigitalBank.setIdUser(VaadinSession.getCurrent().getAttribute("login").toString());
//               ObjectMapper mapper = new ObjectMapper();
//               try {
//                   String op = mapper.writeValueAsString(accountServiceOperationList);
//                   formsDigitalBank.setAccountServiceOperation(op);
//               } catch (JsonProcessingException e) {
//                   e.printStackTrace();
//               }
//
//               formsRestTemplateGlobal.create(formsDigitalBank);
//               UIUtils.dialog("Formulario registrado","success").open();
//               close();
//           }
        });
    }

    private VerticalLayout formDataClient(){
        FormLayout formLayout = new FormLayout();

        TextField name = new TextField();
        name.setWidthFull();

        TextField address = new TextField();
        address.setWidthFull();

        TextField idCard = new TextField();
        idCard.setWidthFull();

        TextField cellphone = new TextField();
        cellphone.setWidthFull();

        TextField homePhone = new TextField();
        homePhone.setWidthFull();

        TextField email = new TextField();
        email.setWidthFull();

        TextField user = new TextField();
        user.setWidthFull();

        formLayout.addFormItem(name,"Nombre cliente");
        formLayout.addFormItem(address,"Direccion");
        formLayout.addFormItem(idCard,"C.I.");
        formLayout.addFormItem(cellphone,"Celular");
        formLayout.addFormItem(homePhone,"Telf. Fijo");
        formLayout.addFormItem(email,"Correo electronico");
        formLayout.addFormItem(user,"Usuario");

        binderDataFormDto.forField(name).bind(DataFormDto::getFullNameClient, DataFormDto::setFullNameClient);
        binderDataFormDto.forField(address).bind(DataFormDto::getAddressHome, DataFormDto::setAddressHome);
        binderDataFormDto.forField(idCard).bind(DataFormDto::getIdCard,DataFormDto::setIdCard);
        binderDataFormDto.forField(cellphone).bind(DataFormDto::getCellphone,DataFormDto::setCellphone);
        binderDataFormDto.forField(homePhone).bind(DataFormDto::getHomePhone,DataFormDto::setHomePhone);
        binderDataFormDto.forField(email).bind(DataFormDto::getEmail,DataFormDto::setEmail);

        binderDataFormDto.readBean(dataFormDtoGlobal.get(0));
        binderDataFormDto.setReadOnly(true);

        binderForms.forField(user).asRequired("Usuario Banca Digital es requerido")
                .bind(Forms::getUserDigitalBank,Forms::setUserDigitalBank);
        binderForms.readBean(formsDigitalBank);

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.add(formLayout);



        return verticalLayout;
    }

    private HorizontalLayout layoutAccounts(){
        accountsSelected  = new HashSet<>();
        List<String> accountList = new ArrayList<>();
        for(DataFormDto data: dataFormDtoGlobal){
            accountList.add(data.getAccount());
        }

        CheckboxGroup<String> checkAccount = new CheckboxGroup<>();
        checkAccount.setItems(accountList);
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSpacing(true);
        Button btnCreate = new Button("Crear");
        btnCreate.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_CONTRAST);

        layout.add(checkAccount,btnCreate);
        checkAccount.addValueChangeListener(event -> {
            accountsSelected = event.getValue();
        });

        btnCreate.addClickListener(event -> {
            if(!accountsSelected.isEmpty()) {
                fillNewServicesAndOperations();
                Button btnSave = new Button("Agregar");
                btnSave.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                dialogServiceOperationDigitalBank = new DialogServiceOperationDigitalBank(servicesList);
                dialogServiceOperationDigitalBank.footer.add(btnSave);
                dialogServiceOperationDigitalBank.open();
                btnSave.addClickListener(click -> {
                    try {
                        AccountServiceOperation accountServiceOperation = new AccountServiceOperation();
                        accountServiceOperation.setId(UUID.randomUUID().toString());

                        accountServiceOperation.setAccount(String.join(" - ", accountsSelected));
                        accountServiceOperation.setServices(dialogServiceOperationDigitalBank.getServices());
                        accountServiceOperation.setExtensionAmount(dialogServiceOperationDigitalBank.extensionAmount.getValue());
                        accountServiceOperation.setDecreaseAmount(dialogServiceOperationDigitalBank.decreaseAmount.getValue());
                        accountServiceOperation.setReasonOpening(dialogServiceOperationDigitalBank.textArea.getValue());
                        Date currentDate = (Date) VaadinSession.getCurrent().getAttribute("current-date");
                        accountServiceOperation.setCreateDate(Util.formatDate(currentDate, "dd/MM/yyyy"));
                        accountServiceOperationList.add(accountServiceOperation);
                        dialogServiceOperationDigitalBank.close();
                        accountServiceOperationList.sort(Comparator.comparing(AccountServiceOperation::getCreateDate).reversed());
                        grid.setItems(accountServiceOperationList);


                        if(binderForms.writeBeanIfValid(formsDigitalBank)){
                            formsDigitalBank.setCategoryTypeForm("VARIOS");
                            formsDigitalBank.setNameTypeForm("BANCA DIGITAL");
                            formsDigitalBank.setIdClient(dataFormDtoGlobal.get(0).getCodeClient());
                            formsDigitalBank.setIdUser(VaadinSession.getCurrent().getAttribute("login").toString());
                            ObjectMapper mapper = new ObjectMapper();
                            try {
                                String op = mapper.writeValueAsString(accountServiceOperationList);
                                formsDigitalBank.setAccountServiceOperation(op);
                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                            }
                            UIUtils.dialog("Cuentas agregadas", "success").open();

                            formsRestTemplateGlobal.create(formsDigitalBank);
                            UIUtils.dialog("Formulario registrado","success").open();

                        }


                    } catch (Exception e) {
                        UIUtils.dialog("Existe un error, revise los datos", "error").open();
                    }
                });
            }else{
                UIUtils.dialog("Seleccione al menos una cuenta","alert").open();
            }
        });

        return layout;
    }

    private void createGridExistingForms()  {
        if(formsDigitalBank.getId()!=null){
            ObjectMapper mapper = new ObjectMapper();
            if(formsDigitalBank.getAccountServiceOperation()!=null) {
                try {
                    accountServiceOperationList = mapper.readValue(formsDigitalBank.getAccountServiceOperation(),
                            new TypeReference<List<AccountServiceOperation>>() {
                    });
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }else{
                accountServiceOperationList = new ArrayList<>();

            }
        }else  accountServiceOperationList = new ArrayList<>();

        grid = new Grid<>();
        grid.setSizeFull();
        accountServiceOperationList.sort(Comparator.comparing(AccountServiceOperation::getCreateDate).reversed());
        grid.setItems(accountServiceOperationList);
        grid.addColumn(AccountServiceOperation::getAccount)
                .setHeader("Cuentas")
                .setFlexGrow(0)
                .setResizable(true)
                .setAutoWidth(true);
        grid.addColumn(AccountServiceOperation::getCreateDate)
                .setHeader("Fecha creacion")
                .setFlexGrow(0)
                .setResizable(true)
                .setAutoWidth(true);
        grid.addColumn(new ComponentRenderer<>(this::createTaskGrid))
                .setFlexGrow(0).setAutoWidth(true);



    }

    private void fillNewServicesAndOperations(){
        servicesList = new ArrayList<>();
        for(Parameter parameter : parameterListGlobal){
            Service s = new Service();
            if(parameter.getCategory().equals("BANCA DIGITAL, SERVICIOS") ||
                    parameter.getCategory().equals("BANCA DIGITAL, OPERACIONES")){
                s.setName(parameter.getName());
                s.setChecked("NO");
                s.setCategory(parameter.getCategory());
                servicesList.add(s);
            }
        }
    }

    private void fillRegisteredServicesAndOperations(String services){
        ObjectMapper mapper = new ObjectMapper();

        try {
            servicesList = mapper.readValue(services, new TypeReference<List<Service>>() {});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }

    private Component createTaskGrid(AccountServiceOperation accountServiceOperation){
        Button btnEdit = new Button("Editar");
        btnEdit.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);
        btnEdit.setIcon(VaadinIcon.FILE_ADD.create());

        Button btnPrint = new Button();
        btnPrint.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_CONTRAST);
        btnPrint.setIcon(VaadinIcon.PRINT.create());

        HorizontalLayout layout = new HorizontalLayout();
        layout.add(btnEdit,btnPrint);

        btnEdit.addClickListener(click -> {
            fillRegisteredServicesAndOperations(accountServiceOperation.getServices());
            Button btnSave = new Button("Modificar");
            btnSave.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            dialogServiceOperationDigitalBank = new DialogServiceOperationDigitalBank(servicesList);
            dialogServiceOperationDigitalBank.footer.add(btnSave);
            dialogServiceOperationDigitalBank.extensionAmount.setValue(accountServiceOperation.getExtensionAmount()!=null?accountServiceOperation.getExtensionAmount():0.0);
            dialogServiceOperationDigitalBank.decreaseAmount.setValue(accountServiceOperation.getDecreaseAmount()!=null?accountServiceOperation.getDecreaseAmount():0.0);
            if(accountServiceOperation.getExtensionAmount()!=null && accountServiceOperation.getExtensionAmount()>0) dialogServiceOperationDigitalBank.extensionAmount.setVisible(true);
            if(accountServiceOperation.getDecreaseAmount()!=null && accountServiceOperation.getDecreaseAmount()>0) dialogServiceOperationDigitalBank.decreaseAmount.setVisible(true);

            dialogServiceOperationDigitalBank.textArea.setValue(accountServiceOperation.getReasonOpening());
            dialogServiceOperationDigitalBank.open();
            btnSave.addClickListener(event -> {
                try {
//                    accountServiceOperation.setAccount(String.join(" - ",accountsSelected));
                    accountServiceOperation.setServices(dialogServiceOperationDigitalBank.getServices());
                    accountServiceOperation.setExtensionAmount(dialogServiceOperationDigitalBank.extensionAmount.getValue());
                    accountServiceOperation.setDecreaseAmount(dialogServiceOperationDigitalBank.decreaseAmount.getValue());
                    accountServiceOperation.setReasonOpening(dialogServiceOperationDigitalBank.textArea.getValue());
//                    Date currentDate = (Date) VaadinSession.getCurrent().getAttribute("current-date");
//                    accountServiceOperation.setCreateDate(Util.formatDate(currentDate,"dd/MM/yyyy"));
                    accountServiceOperationList.removeIf(f -> f.getId().equals(accountServiceOperation.getId()));

                    accountServiceOperationList.add(accountServiceOperation);
                    dialogServiceOperationDigitalBank.close();
                    accountServiceOperationList.sort(Comparator.comparing(AccountServiceOperation::getCreateDate).reversed());
                    grid.setItems(accountServiceOperationList);

                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        String op = mapper.writeValueAsString(accountServiceOperationList);
                        formsDigitalBank.setAccountServiceOperation(op);
                        formsRestTemplateGlobal.create(formsDigitalBank);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }

                    UIUtils.dialog("Cuenta actualizada", "success").open();


                }catch(Exception e){
                    UIUtils.dialog("Existe un error, revise los datos", "alert").open();
                }
            });
        });

        btnPrint.addClickListener(click -> {
            FormReportView report = new FormReportView(formsDigitalBank.getIdClient(),accountServiceOperation.getId(),
                    formsDigitalBank.getNameTypeForm(),formsDigitalBank.getCategoryTypeForm(),formsRestTemplateGlobal,"","");
            report.open();
        });

        return layout;
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
