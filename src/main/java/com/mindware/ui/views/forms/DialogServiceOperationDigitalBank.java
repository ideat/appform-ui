package com.mindware.ui.views.forms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindware.backend.entity.Service;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
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
import com.vaadin.flow.theme.lumo.Lumo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CssImport("./styles/my-dialog.css")
public class DialogServiceOperationDigitalBank extends Dialog {

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
    private List<Service> operationListGlobal;

    private List<String> serviceStringList;
    private List<String> operationStringList;

    public Set<String> servicesSelected;
    public Set<String> operationsSelected;

    private List<String> serviceInitialSelected;
    private List<String> operationInitialSelected;

    public TextArea textArea;

    public  NumberField extensionAmount;
    public  NumberField decreaseAmount;

    public DialogServiceOperationDigitalBank(List<Service> serviceList){
        setDraggable(true);
        setModal(false);
        setResizable(true);

        fillServicesOperations(serviceList);

        // Dialog theming
        getElement().getThemeList().add("my-dialog");
        setWidth("800px");

        // Accessibility
        getElement().setAttribute("aria-labelledby", "dialog-title");

        // Header
        H2 title = new H2("Servicios y Operaciones");
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
        content = new VerticalLayout(layoutServiceOperations(), textArea);
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
//        maximise();
    }

    public String getServices()  {
        List<Service> serviceState = new ArrayList<>();

        for(String s:servicesSelected){
            Service service = serviceListGlobal.stream()
                    .filter(f -> f.getName().equals(s))
                    .collect(Collectors.toList()).get(0);
            service.setChecked("SI");
            serviceListGlobal.removeIf(d -> d.getName().equals(s));
            serviceListGlobal.add(service);
        }

        for(String s:operationsSelected){
            Service service = operationListGlobal.stream()
                    .filter(f -> f.getName().equals(s))
                    .collect(Collectors.toList()).get(0);
            service.setChecked("SI");
            operationListGlobal.removeIf(d -> d.getName().equals(s));
            operationListGlobal.add(service);
        }

        serviceState.addAll(serviceListGlobal);
        serviceState.addAll(operationListGlobal);

        ObjectMapper mapper = new ObjectMapper();
        String result = "";
        try {
            result = mapper.writeValueAsString(serviceState);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return  result;
    }



    private void fillServicesOperations(List<Service> serviceList){
        serviceListGlobal = serviceList.stream()
            .filter(s -> s.getCategory().equals("BANCA DIGITAL, SERVICIOS"))
            .collect(Collectors.toList());
        operationListGlobal = serviceList.stream()
                .filter(s -> s.getCategory().equals("BANCA DIGITAL, OPERACIONES"))
                .collect(Collectors.toList());

        serviceStringList = serviceListGlobal.stream()
                .map(Service::getName)
                .collect(Collectors.toList());
        operationStringList = operationListGlobal.stream()
                .map(Service::getName)
                .collect(Collectors.toList());

        serviceInitialSelected = serviceListGlobal.stream()
                .filter(s -> s.getChecked().equals("SI"))
                .map(Service::getName)
                .collect(Collectors.toList());
        servicesSelected = Set.copyOf(serviceInitialSelected);

        operationInitialSelected = operationListGlobal.stream()
                .filter(s -> s.getChecked().equals("SI"))
                .map(Service::getName)
                .collect(Collectors.toList());
        operationsSelected = Set.copyOf(operationInitialSelected);

    }

    private VerticalLayout createLayoutService(){

        Checkbox checkboxService = new Checkbox("Seleccione Todo");
        Set<String> setService = new HashSet<>(serviceStringList);

        CheckboxGroup<String> checkboxGroupService = new CheckboxGroup<>();
        checkboxGroupService.setLabel("Servicios");
        checkboxGroupService.setItems(setService);
        checkboxGroupService.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);
        checkboxGroupService.setHelperText("Seleccione los Servicios");

        checkboxGroupService.select(serviceInitialSelected);

        checkboxGroupService.addValueChangeListener(event -> {
           if(event.getValue().size() == serviceStringList.size()){
               checkboxService.setValue(true);
               checkboxService.setIndeterminate(false);
           }else if(event.getValue().size() == 0){
               checkboxService.setValue(false);
               checkboxService.setIndeterminate(false);
           }else checkboxService.setIndeterminate(true);
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

        checkboxService.addValueChangeListener(event -> {
            if (checkboxService.getValue()) {
                checkboxGroupService.setValue(setService);
            } else {
                checkboxGroupService.deselectAll();
            }
        });

        VerticalLayout layout = new VerticalLayout();
        layout.add(checkboxService, checkboxGroupService, extensionAmount, decreaseAmount);
        
        return layout;
    }


    private VerticalLayout createLayoutOperations(){

        Checkbox checkboxOperation = new Checkbox("Seleccione Todo");
        Set<String> setOperation = new HashSet<>(operationStringList);

        CheckboxGroup<String> checkboxGroupOperation = new CheckboxGroup<>();
        checkboxGroupOperation.setLabel("Operaciones");
        checkboxGroupOperation.setItems(setOperation);
        checkboxGroupOperation.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);
        checkboxGroupOperation.setHelperText("Seleccione los Servicios");

        checkboxGroupOperation.select(operationInitialSelected);

        checkboxGroupOperation.addValueChangeListener(event -> {
            if(event.getValue().size() == operationStringList.size()){
                checkboxOperation.setValue(true);
                checkboxOperation.setIndeterminate(false);
            }else if(event.getValue().size() == 0){
                checkboxOperation.setValue(false);
                checkboxOperation.setIndeterminate(false);
            }else checkboxOperation.setIndeterminate(true);
            operationsSelected = event.getValue();
        });

        checkboxOperation.addValueChangeListener(event -> {
            if (checkboxOperation.getValue()) {
                checkboxGroupOperation.setValue(setOperation);
            } else {
                checkboxGroupOperation.deselectAll();
            }
        });

        VerticalLayout layout = new VerticalLayout();
        layout.add(checkboxOperation, checkboxGroupOperation);

        return layout;
    }

    private HorizontalLayout layoutServiceOperations() {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.add(createLayoutService(),createLayoutOperations());

        return horizontalLayout;

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
