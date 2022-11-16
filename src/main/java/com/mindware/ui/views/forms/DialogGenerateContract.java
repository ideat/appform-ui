package com.mindware.ui.views.forms;

import com.mindware.backend.entity.Signatory;
import com.mindware.backend.entity.netbank.dto.GbageDto;
import com.mindware.backend.rest.contract.ContractRestTemplate;
import com.mindware.backend.rest.forms.FormsRestTemplate;
import com.mindware.ui.util.UIUtils;
import com.vaadin.componentfactory.lookupfield.AbstractLookupField;
import com.vaadin.componentfactory.lookupfield.LookupField;
import com.vaadin.componentfactory.theme.EnhancedDialogVariant;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.Lumo;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;

public class DialogGenerateContract extends Dialog {

    public String DOCK = "dock";
    public String FULLSCREEN = "fullscreen";

    private boolean isDocked = false;
    private boolean isFullScreen = false;

    private Header header;
    private Button min;
    private Button max;
    private Button btnGenerateContract;
    private DatePicker dateContract;
    private VerticalLayout content;
    public Footer footer;

    private LookupField<Signatory> signatoryLookupField;

    public DialogGenerateContract(List<Signatory> signatoryList, ContractRestTemplate contractRestTemplate, GbageDto gbageDto, FormsRestTemplate formsRestTemplate){
        setDraggable(true);
        setModal(false);
        setResizable(true);

        // Dialog theming
        getElement().getThemeList().add("my-dialog");
        setWidth("800px");

        // Accessibility
        getElement().setAttribute("aria-labelledby", "dialog-title");

        // Header
        String textTitle="GENERAR CONTRATO";
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

        btnGenerateContract = new Button("Guardar");

        //Content

        content = new VerticalLayout(createLayout(signatoryList,gbageDto,formsRestTemplate,contractRestTemplate));
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

    private VerticalLayout createLayout(List<Signatory> signatoryList, GbageDto gbageDto, FormsRestTemplate formsRestTemplate, ContractRestTemplate contractRestTemplate){
        VerticalLayout layout = new VerticalLayout();

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        signatoryLookupField = new LookupField<>(Signatory.class);
        signatoryLookupField.setWidthFull();
        signatoryLookupField.setItems(signatoryList);
        signatoryLookupField.setI18n(new AbstractLookupField.LookupFieldI18n()
                .setSearcharialabel("Click para buscar")
                .setSelect("Seleccionar")
                .setSearch("Buscar")
                .setCancel("Cancelar")
        );
        signatoryLookupField.setLabel("Representante Legal");

        signatoryLookupField.getGrid().removeAllColumns();
        signatoryLookupField.getGrid().addColumn(Signatory::getFullName).setHeader("Nombre Representante");
        signatoryLookupField.getGrid().addColumn(Signatory::getPlaza).setHeader("Código Plaza");
        signatoryLookupField.getGrid().addColumn(Signatory::getPosition).setHeader("Cargo");

        signatoryLookupField.addThemeVariants(EnhancedDialogVariant.SIZE_MEDIUM);

        dateContract = new DatePicker("Fecha contrato");
        dateContract.setWidthFull();
        dateContract.setLocale(new Locale("es","BO"));
        dateContract.setI18n(UIUtils.spanish());

        btnGenerateContract = new Button("Generar");
        btnGenerateContract.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnGenerateContract.addClickListener(event -> {
            FormReportView report = null;
            String login = VaadinSession.getCurrent().getAttribute("login").toString();
            try {
                if(gbageDto.getAccountName().equals("CAJA-AHORRO")) {
                           int years = getYears(gbageDto);
                           if(years < 18){
                               report = new FormReportView(gbageDto.getSecundaryCage(), gbageDto.getAccountCode(),
                                       "CONTRATO", gbageDto.getAccountName(), formsRestTemplate,
                                       gbageDto.getTypeAccount().trim(), signatoryLookupField.getValue().getPlaza().toString(), login, contractRestTemplate, "SI","SI");
                           }else{
                               report = new FormReportView(gbageDto.getGbagecage(), gbageDto.getAccountCode(),
                                       "CONTRATO", gbageDto.getAccountName(), formsRestTemplate,
                                       gbageDto.getTypeAccount().trim(), signatoryLookupField.getValue().getPlaza().toString(), login, contractRestTemplate, "NO","NO");
                           }

                }else {
                    report = new FormReportView(gbageDto.getGbagecage(), gbageDto.getAccountCode(),
                            "CONTRATO", gbageDto.getAccountName(),
                            formsRestTemplate, "", signatoryLookupField.getValue().getPlaza().toString(),login, contractRestTemplate, "NO","NO");
                }
                report.open();
            }catch (Exception e){

            }
        });

        horizontalLayout.add(signatoryLookupField);
        horizontalLayout.add(dateContract);
        horizontalLayout.add(btnGenerateContract);
        horizontalLayout.setVerticalComponentAlignment(FlexComponent.Alignment.END,btnGenerateContract);

        layout.add(horizontalLayout);

        return layout;

    }

    private int getYears(GbageDto gbageDto) {
        LocalDate currentDate = LocalDate.now();
        LocalDate birthDate = gbageDto.getGbagefnac().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        Period period = Period.between(currentDate, birthDate);
        return Math.abs(period.getYears());
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
