package com.mindware.ui.views.forms;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindware.backend.entity.Beneficiary;
import com.mindware.ui.components.FlexBoxLayout;
import com.mindware.ui.layout.size.Size;
import com.mindware.ui.util.LumoStyles;
import com.mindware.ui.util.UIUtils;
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
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.theme.lumo.Lumo;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;

@CssImport("./styles/my-dialog.css")
public class BeneficiaryView extends Dialog {
    public String DOCK = "dock";
    public String FULLSCREEN = "fullscreen";

    private boolean isDocked = false;
    private boolean isFullScreen = false;

    private Header header;
    private Button min;
    private Button max;

//    public Button btnSave;

    private VerticalLayout content;
//    private Footer footer;
    private List<Beneficiary> beneficiaryList = new ArrayList<>();

    private BeneficiaryRegisterView beneficiaryRegisterView;
    private Grid<Beneficiary> grid;

    public BeneficiaryView(String beneficiary){
        setDraggable(true);
        setModal(false);
        setResizable(true);

        // Dialog theming
        getElement().getThemeList().add("my-dialog");
        setWidth("800px");

        // Accessibility
        getElement().setAttribute("aria-labelledby", "dialog-title");

        // Header
        H2 title = new H2("Formulario Apertura de Ahorro");
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
        HorizontalLayout flex = new HorizontalLayout();
        
        Button btnSearch = new Button("Buscar");
        btnSearch.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnSearch.setIcon(VaadinIcon.SEARCH.create());

        TextField search = new TextField();


        flex.add(search,btnSearch);
        flex.setAlignItems(FlexComponent.Alignment.START);
        flex.setSpacing(true);

        btnSearch.addClickListener(event -> {

            Beneficiary newItem = new Beneficiary();
            beneficiaryRegisterView = new BeneficiaryRegisterView(newItem);
            Footer footer = new Footer();
            Button save = new Button("Guardar");
            save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            footer.add(save);
            save.addClickListener(e -> {
                if(beneficiaryRegisterView.save()) {
                    beneficiaryRegisterView.close();
                    beneficiaryList.add(beneficiaryRegisterView.beneficiaryGlobal);
                    grid.setItems(beneficiaryList);
                }

            });
            beneficiaryRegisterView.add(footer);
            beneficiaryRegisterView.open();



        });

        beneficiaryList = getBeneficiaryList(beneficiary);

        gridBeneficiary();
        content = new VerticalLayout(flex,grid);
        content.addClassName("dialog-content");
        content.setAlignItems(FlexComponent.Alignment.STRETCH);
        add(content);

        // Footer
//        btnSave = new Button("Guardar");
//        btnSave.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
//        btnSave.addClickListener(event -> {
//
//        });

        Button attachFiles = new Button(VaadinIcon.PAPERCLIP.create());
        Button discardDraft = new Button(VaadinIcon.TRASH.create());

//        footer = new Footer(btnSave, attachFiles, discardDraft);
//        add(footer);

        // Button theming
        for (Button button : new Button[] { min, max, close, attachFiles, discardDraft }) {
            button.addThemeVariants(ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_TERTIARY);
        }
    }

    @SneakyThrows
    public String getFinalBeneficiaryList(){
        ObjectMapper mapper = new ObjectMapper();
        String result = mapper.writeValueAsString(beneficiaryList);
        return result;
    }

    private void gridBeneficiary(){
        grid = new Grid();
        grid.setItems(beneficiaryList);
        grid.addColumn(Beneficiary::getIdCard)
                .setFlexGrow(1)
                .setAutoWidth(true)
                .setHeader("Carnet/Nit");
        grid.addColumn(Beneficiary::getFullName)
                .setFlexGrow(1)
                .setAutoWidth(true)
                .setHeader("Nombre completo");
        grid.addColumn(new ComponentRenderer<>(this::createButtons))
                .setFlexGrow(0).setAutoWidth(true);

//        return grid;

    }

    private FormLayout layout(){
        FormLayout formBeneficiary = new FormLayout();


        return formBeneficiary;
    }

    @SneakyThrows
    private List<Beneficiary> getBeneficiaryList(String beneficiary){
        ObjectMapper mapper = new ObjectMapper();

        List<Beneficiary> list = mapper.readValue(beneficiary, new TypeReference<List<Beneficiary>>() {});

        return list;
    }

    private Component createButtons(Beneficiary beneficiary){
        Button btnEdit = new Button();
        btnEdit.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_SUCCESS);
        btnEdit.setIcon(VaadinIcon.FOLDER_OPEN.create());

        Button btnDelete = new Button();
        btnDelete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        btnDelete.setIcon(VaadinIcon.TRASH.create());

        if(beneficiary.getId()==null) {
            btnEdit.setEnabled(false);
            btnDelete.setEnabled(false);
        }else{
            btnEdit.setEnabled(true);
            btnDelete.setEnabled(true);
        }

        HorizontalLayout layout = new HorizontalLayout();
        layout.add(btnEdit,btnDelete);
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
