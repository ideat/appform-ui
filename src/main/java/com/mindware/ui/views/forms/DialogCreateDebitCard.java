package com.mindware.ui.views.forms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindware.backend.entity.Service;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.theme.lumo.Lumo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CssImport("./styles/my-dialog.css")
public class DialogCreateDebitCard extends Dialog {
    public String DOCK = "dock";
    public String FULLSCREEN = "fullscreen";

    private boolean isDocked = false;
    private boolean isFullScreen = false;

    private Header header;
    private Button min;
    private Button max;

    private VerticalLayout content;
    public Footer footer;

    private List<Service> serviceListGlobal;
    private List<String> serviceStringList;
    public Set<String> servicesSelected;
    private List<String> serviceInitialSelected;
    public TextArea textArea;
    public ComboBox<String> cmbAccountSavingBank;

    public NumberField extensionAmount;
    public  NumberField decreaseAmount;
    public TextField numberDebitCard;

    public DialogCreateDebitCard(List<Service> serviceList, List<String> accountSavingBankList){

        setDraggable(true);
        setModal(false);
        setResizable(true);

        fillServices(serviceList);

        // Dialog theming
        getElement().getThemeList().add("my-dialog");
        setWidth("800px");

        // Accessibility
        getElement().setAttribute("aria-labelledby", "dialog-title");

        // Header
        H2 title = new H2("Servicios Tarjeta de Debito");
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

        extensionAmount = new NumberField("Ampliacion limite Bs");
        extensionAmount.setWidthFull();
        extensionAmount.setVisible(false);
        decreaseAmount = new NumberField("Disminucion limite Bs");
        decreaseAmount.setWidthFull();
        decreaseAmount.setVisible(false);

        textArea = new TextArea("Motivos para habilitacion de servicios");
        textArea.setWidthFull();

        numberDebitCard = new TextField("Numero de tarjeta");
        numberDebitCard.setWidthFull();

        cmbAccountSavingBank = new ComboBox<>("Debitar los cargos de la Caja ahorro:");
        cmbAccountSavingBank.setItems(accountSavingBankList);
        cmbAccountSavingBank.setWidthFull();

        content = new VerticalLayout(createLayoutService(), numberDebitCard, textArea, cmbAccountSavingBank);
        content.addClassName("dialog-content");
        content.setAlignItems(FlexComponent.Alignment.STRETCH);
        add(content);

        // Footer

        footer = new Footer();
        add(footer);

        // Button theming
        for (Button button : new Button[] { min, max, close }) {
            button.addThemeVariants(ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_TERTIARY);
        }
    }

    public String getServices(){
        List<Service> serviceState = new ArrayList<>();

        for(String s:servicesSelected){
            Service service = serviceListGlobal.stream()
                    .filter(f -> f.getName().equals(s))
                    .collect(Collectors.toList()).get(0);
            service.setChecked("SI");
            serviceListGlobal.removeIf(d -> d.getName().equals(s));
            serviceListGlobal.add(service);
        }

        serviceState.addAll(serviceListGlobal);

        ObjectMapper mapper = new ObjectMapper();
        String result = "";
        try {
            result = mapper.writeValueAsString(serviceState);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return  result;
    }

    private void fillServices(List<Service> serviceList){
        serviceListGlobal = serviceList.stream()
                .filter(s -> s.getCategory().equals("TARJETA DEBITO, SERVICIOS"))
                .collect(Collectors.toList());
        serviceStringList = serviceListGlobal.stream()
                .map(Service::getName)
                .collect(Collectors.toList());
        serviceInitialSelected = serviceListGlobal.stream()
                .filter(s -> s.getChecked().equals("SI"))
                .map(Service::getName)
                .collect(Collectors.toList());
        servicesSelected = Set.copyOf(serviceInitialSelected);
    }

    private HorizontalLayout createLayoutService(){
        Set<String> setService = new HashSet<>(serviceStringList);
        CheckboxGroup<String> checkboxGroupService = new CheckboxGroup<>();
        checkboxGroupService.setLabel("Servicios Solicitados");
        checkboxGroupService.setItems(setService);
        checkboxGroupService.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);
        checkboxGroupService.setHelperText("Seleccione los Servicios");

        checkboxGroupService.select(serviceInitialSelected);

        checkboxGroupService.addValueChangeListener(event -> {
            servicesSelected = event.getValue();

            if(event.getValue().contains("AMPLIACION DE LIMITE HASTA")){
                extensionAmount.setVisible(true);
            }else {
                extensionAmount.setVisible(false);
                extensionAmount.setValue(0.0);
            }

            if(event.getValue().contains("DISMINUCION DE LIMITE HASTA")){
                decreaseAmount.setVisible(true);
            }else {
                decreaseAmount.setVisible(false);
                decreaseAmount.setValue(0.0);
            }
        });
        VerticalLayout layout2 = new VerticalLayout();
        layout2.setWidth("50%");
        layout2.add(extensionAmount, decreaseAmount);

        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidth("90%");
        layout.add( checkboxGroupService, layout2);

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
