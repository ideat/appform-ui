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
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.Lumo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@CssImport("./styles/my-dialog.css")
public class DialogServiceDebitCard extends Dialog {

    public String DOCK = "dock";
    public String FULLSCREEN = "fullscreen";

    private boolean isDocked = false;
    private boolean isFullScreen = false;

    private Header header;
    private Button min;
    private Button max;
    private Button btnSave;
    private Button discardDraft;

    private VerticalLayout content;
    private Footer footer;

    private BeanValidationBinder<Forms> binderForms;
    private BeanValidationBinder<DataFormDto> binderDataFormDto;
    private List<AccountServiceOperation> accountServiceOperationList;
    private List<DataFormDto> dataFormDtoGlobal;

    private List<Parameter> parameterListGlobal;
    private Forms formsDebitCard;
    private FormsRestTemplate formsRestTemplateGlobal;

    private Grid<AccountServiceOperation> grid;

    private List<Service> servicesList;
    private DialogCreateDebitCard dialogCreateDebitCard;
    private List<String> accounSavingBankList;

    public DialogServiceDebitCard(List<DataFormDto> dataFormDto, List<Parameter> parameterList, FormsRestTemplate formsRestTemplate){

        setDraggable(true);
        setModal(false);
        setResizable(true);

        parameterListGlobal = parameterList;
        dataFormDtoGlobal = dataFormDto;
        formsDebitCard = formsRestTemplate.findByIdClientAndTypeFormAndCategoryTypeForm(dataFormDto.get(0).getCodeClient(),
                "SERVICIOS TD","VARIOS");
        formsRestTemplateGlobal = formsRestTemplate;

        accounSavingBankList = dataFormDto.stream()
                .map(a -> a.getAccount())
                .collect(Collectors.toList());

        // Dialog theming
        getElement().getThemeList().add("my-dialog");
        setWidth("800px");

        // Accessibility
        getElement().setAttribute("aria-labelledby", "dialog-title");

        // Header
        String textTitle="SERVICIOS TARJETA DE DEBITO";
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

        btnSave = new Button("Guardar");
        discardDraft = new Button(VaadinIcon.TRASH.create());

        //Content

        binderDataFormDto = new BeanValidationBinder(DataFormDto.class);
        binderForms = new BeanValidationBinder<>(Forms.class);

        createGrid();

        content = new VerticalLayout(formDataClient(),grid);
        content.addClassName("dialog-content");
        content.setAlignItems(FlexComponent.Alignment.STRETCH);
        add(content);

        //Footer

        btnSave.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        discardDraft.addThemeVariants(ButtonVariant.LUMO_ERROR,ButtonVariant.LUMO_TERTIARY);
        footer = new Footer(btnSave,  discardDraft);
        add(footer);

        // Button theming
        for (Button button : new Button[] { min, max, close }) {
            button.addThemeVariants(ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_TERTIARY);
        }
        maximise();

        btnSave.addClickListener(event -> {
            if(accountServiceOperationList.size() > 0){
                formsDebitCard.setCategoryTypeForm("VARIOS");
                formsDebitCard.setNameTypeForm("SERVICIOS TD");
                formsDebitCard.setIdClient(dataFormDto.get(0).getCodeClient());
                formsDebitCard.setIdUser(VaadinSession.getCurrent().getAttribute("login").toString());

                ObjectMapper mapper = new ObjectMapper();
                try {
                    String op = mapper.writeValueAsString(accountServiceOperationList);
                    formsDebitCard.setAccountServiceOperation(op);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

                formsRestTemplate.create(formsDebitCard);
                UIUtils.dialog("Formulario registrado","success").open();
                close();
            }
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

        formLayout.addFormItem(name,"Nombre cliente");
        formLayout.addFormItem(address,"DirecciÓn");
        formLayout.addFormItem(idCard,"C.I.");
        formLayout.addFormItem(cellphone,"Celular");
        formLayout.addFormItem(homePhone,"Telf. Fijo");

        binderDataFormDto.forField(name).bind(DataFormDto::getFullNameClient, DataFormDto::setFullNameClient);
        binderDataFormDto.forField(address).bind(DataFormDto::getAddressHome, DataFormDto::setAddressHome);
        binderDataFormDto.forField(idCard).bind(DataFormDto::getIdCard,DataFormDto::setIdCard);
        binderDataFormDto.forField(cellphone).bind(DataFormDto::getCellphone,DataFormDto::setCellphone);
        binderDataFormDto.forField(homePhone).bind(DataFormDto::getHomePhone,DataFormDto::setHomePhone);

        binderDataFormDto.readBean(dataFormDtoGlobal.get(0));
        binderDataFormDto.setReadOnly(true);

        VerticalLayout layout = new VerticalLayout();

        Button btnCreate = new Button("Nuevo");
        btnCreate.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_CONTRAST);
        layout.add(formLayout,btnCreate);

        btnCreate.addClickListener(event -> {
            fillNewServicesAndOperations();
            Button btnCancel = new Button("Cancelar");
            btnCancel.addThemeVariants(ButtonVariant.LUMO_ERROR);
            btnCancel.addClickListener(close -> dialogCreateDebitCard.close());

            Button btnNew = new Button("Agregar");
            btnNew.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            dialogCreateDebitCard = new DialogCreateDebitCard(servicesList,accounSavingBankList);
            dialogCreateDebitCard.footer.add(btnNew, btnCancel);
            dialogCreateDebitCard.open();
            btnNew.addClickListener(click -> {
                try {
                    if (dialogCreateDebitCard.numberDebitCard.getValue() == null ||
                            dialogCreateDebitCard.numberDebitCard.getValue().isEmpty()) {
                        UIUtils.dialog("Debe ingresar el numero de tarjeta", "alert").open();
                        return;
                    }
                    if (dialogCreateDebitCard.textArea.getValue() == null || dialogCreateDebitCard.textArea.getValue().isEmpty()) {
                        UIUtils.dialog("Debe indicar un texto referente a los motivos del servicio solicitado", "alert").open();
                        return;
                    }
//                    if (dialogCreateDebitCard.servicesSelected.isEmpty()) {
//                        UIUtils.dialog("Debe seleccionar un servicio", "alert").open();
//                        return;
//                    }
                    AccountServiceOperation accountServiceOperation = new AccountServiceOperation();
                    accountServiceOperation.setId(UUID.randomUUID().toString());

                    accountServiceOperation.setAccount(dialogCreateDebitCard.numberDebitCard.getValue());
                    accountServiceOperation.setServices(dialogCreateDebitCard.getServices());

                    accountServiceOperation.setExtensionAmount(dialogCreateDebitCard.extensionAmount.getValue());
                    accountServiceOperation.setDecreaseAmount(dialogCreateDebitCard.decreaseAmount.getValue());
                    accountServiceOperation.setReasonOpening(dialogCreateDebitCard.textArea.getValue());
                    accountServiceOperation.setAccountSavingBank(
                            dialogCreateDebitCard.cmbAccountSavingBank.getValue()!=null?dialogCreateDebitCard.cmbAccountSavingBank.getValue():"");
                    Date currentDate = (Date) VaadinSession.getCurrent().getAttribute("current-date");
                    accountServiceOperation.setCreateDate(Util.formatDate(currentDate, "dd/MM/yyyy"));

                    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
                    Date date = new Date();
                    String hour = formatter.format(date);
                    accountServiceOperation.setHourCreate(hour);

                    accountServiceOperationList.add(accountServiceOperation);
                    dialogCreateDebitCard.close();
                    grid.setItems(accountServiceOperationList);
                    UIUtils.dialog("Cuentas agregadas", "success").open();
                }catch (Exception e) {
                    UIUtils.dialog("Existe un error, revise los datos", "error").open();
                }
            });
        });

        return layout;
    }

    private void createGrid(){
        if(formsDebitCard.getId()!=null){
            ObjectMapper mapper = new ObjectMapper();
            if(formsDebitCard.getAccountServiceOperation()!=null){
                try {
                    accountServiceOperationList = mapper.readValue(formsDebitCard.getAccountServiceOperation(),
                            new TypeReference<List<AccountServiceOperation>>() {});
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }else{
                accountServiceOperationList = new ArrayList<>();
            }
        }else accountServiceOperationList = new ArrayList<>();


        grid = new Grid();
        grid.setSizeFull();
        grid.setItems(accountServiceOperationList);
        grid.addColumn(AccountServiceOperation::getAccount)
                .setHeader("Número de tarjeta")
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

    private Component createTaskGrid(AccountServiceOperation accountServiceOperation){
        Button btnEdit = new Button("Editar");
        btnEdit.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_SUCCESS);
        btnEdit.setIcon(VaadinIcon.FILE_REFRESH.create());

        Button btnPrint = new Button("Servicios");
        btnPrint.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_CONTRAST);
        btnPrint.setIcon(VaadinIcon.PRINT.create());

        Button btnPrintDeliver = new Button("Entrega");
        btnPrintDeliver.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_CONTRAST);
        btnPrintDeliver.setIcon(VaadinIcon.PRINT.create());

        HorizontalLayout layout = new HorizontalLayout();
        layout.add(btnEdit,btnPrint,btnPrintDeliver);

        btnEdit.addClickListener(event -> {
            fillRegisteredServicesAndOperations(accountServiceOperation.getServices());
            Button btnSave = new Button("Modificar");
            btnSave.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            Button btnCancel = new Button("Cancelar");
            btnCancel.addThemeVariants(ButtonVariant.LUMO_ERROR,ButtonVariant.LUMO_SMALL);
            btnCancel.addClickListener(buttonClickEvent -> dialogCreateDebitCard.close());

            dialogCreateDebitCard = new DialogCreateDebitCard(servicesList,accounSavingBankList);
            dialogCreateDebitCard.footer.add(btnSave,btnCancel);
            dialogCreateDebitCard.extensionAmount.setValue(accountServiceOperation.getExtensionAmount()!=null?accountServiceOperation.getExtensionAmount():0.0);
            dialogCreateDebitCard.decreaseAmount.setValue(accountServiceOperation.getDecreaseAmount()!=null?accountServiceOperation.getDecreaseAmount():0.0);
            dialogCreateDebitCard.numberDebitCard.setValue(accountServiceOperation.getAccount());
            if(accountServiceOperation.getExtensionAmount()!=null && accountServiceOperation.getExtensionAmount()>0)
                dialogCreateDebitCard.extensionAmount.setVisible(true);
            if(accountServiceOperation.getDecreaseAmount()!=null && accountServiceOperation.getDecreaseAmount()>0)
                dialogCreateDebitCard.decreaseAmount.setVisible(true);

            dialogCreateDebitCard.textArea.setValue(accountServiceOperation.getReasonOpening());
            dialogCreateDebitCard.cmbAccountSavingBank.setValue(accountServiceOperation.getAccountSavingBank());
            dialogCreateDebitCard.open();
            btnSave.addClickListener(click -> {
                try {
                    accountServiceOperation.setServices(dialogCreateDebitCard.getServices());
                    accountServiceOperation.setReasonOpening(dialogCreateDebitCard.textArea.getValue());
                    accountServiceOperation.setDecreaseAmount(dialogCreateDebitCard.decreaseAmount.getValue());
                    accountServiceOperation.setExtensionAmount(dialogCreateDebitCard.extensionAmount.getValue());
                    accountServiceOperation.setAccount(dialogCreateDebitCard.numberDebitCard.getValue());

                    accountServiceOperationList.removeIf(f -> f.getId().equals(accountServiceOperation.getId()));

                    accountServiceOperationList.add(accountServiceOperation);
                    dialogCreateDebitCard.close();
                    grid.setItems(accountServiceOperationList);
                    UIUtils.dialog("Servicio TD actualizados, Guarde el formulario", "success").open();
                }catch(Exception e){
                    UIUtils.dialog("Existe un error, revise los datos", "alert").open();
                }
            });


        });

        btnPrint.addClickListener(click -> {
            FormReportView report = new FormReportView(formsDebitCard.getIdClient(),accountServiceOperation.getId(),
                    formsDebitCard.getNameTypeForm(),formsDebitCard.getCategoryTypeForm(),formsRestTemplateGlobal,"","");
            report.open();
        });

        btnPrintDeliver.addClickListener(click -> {
            if(formsDebitCard.getId()!=null) {
                if ( accountServiceOperation.getDeliverDate()==null || accountServiceOperation.getDeliverDate().isEmpty() || accountServiceOperation.getDeliverDate().equals("null")) {
                    Date currentDate = (Date) VaadinSession.getCurrent().getAttribute("current-date");
                    accountServiceOperation.setDeliverDate(Util.formatDate(currentDate, "dd/MM/yyyy"));
                    accountServiceOperationList.removeIf(f -> f.getId().equals(accountServiceOperation.getId()));
                    accountServiceOperationList.add(accountServiceOperation);
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        String op = mapper.writeValueAsString(accountServiceOperationList);
                        formsDebitCard.setAccountServiceOperation(op);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }

                    formsRestTemplateGlobal.create(formsDebitCard);
                    UIUtils.showNotification("Preparando reporte");


                }
            }else{
                UIUtils.dialog("Registre la tarjeta guardando los cambios","alert").open();
                return;
            }
            FormReportView report = new FormReportView(formsDebitCard.getIdClient(),accountServiceOperation.getId(),
                    formsDebitCard.getNameTypeForm(),formsDebitCard.getCategoryTypeForm(),formsRestTemplateGlobal,"DELIVER","");
            report.open();

        });

        return layout;
    }

    private void fillRegisteredServicesAndOperations(String services){
        ObjectMapper mapper = new ObjectMapper();

        try {
            servicesList = mapper.readValue(services, new TypeReference<List<Service>>() {});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }

    private void fillNewServicesAndOperations(){
        servicesList = new ArrayList<>();
        for(Parameter parameter : parameterListGlobal){
            Service s = new Service();
            if(parameter.getCategory().equals("TARJETA DEBITO, SERVICIOS") ){
                s.setName(parameter.getName());
                s.setChecked("NO");
                s.setCategory(parameter.getCategory());
                servicesList.add(s);
            }

        }
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
