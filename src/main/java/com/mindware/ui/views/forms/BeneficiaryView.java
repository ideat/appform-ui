package com.mindware.ui.views.forms;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindware.backend.entity.Beneficiary;
import com.mindware.backend.entity.netbank.Gbcon;
import com.mindware.backend.entity.netbank.dto.GbageLabDto;
import com.mindware.backend.rest.netbank.GbageLabDtoRestTemplate;
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
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.theme.lumo.Lumo;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@CssImport("./styles/my-dialog.css")
public class BeneficiaryView extends Dialog {
    public String DOCK = "dock";
    public String FULLSCREEN = "fullscreen";

    private boolean isDocked = false;
    private boolean isFullScreen = false;

    private Header header;
    private Button min;
    private Button max;

    private VerticalLayout content;

    private List<Beneficiary> beneficiaryList = new ArrayList<>();

    private BeneficiaryRegisterView beneficiaryRegisterView;
    private Grid<Beneficiary> grid;
    private Grid<GbageLabDto> grid2;
    private List<GbageLabDto> gbageLabDtoList = new ArrayList<>();
    private GbageLabDtoRestTemplate gbageLabDtoRestTemplateGlobal;

    private BeneficiarySearch beneficiarySearch;

    private RadioButtonGroup<String> radioSearch = new RadioButtonGroup<>();
    private List<Gbcon> gbconListGlobal;

    public BeneficiaryView(String beneficiary, GbageLabDtoRestTemplate gbageLabDtoRestTemplate, List<Gbcon> gbconList){
        setDraggable(true);
        setModal(false);
        setResizable(true);

        gbageLabDtoRestTemplateGlobal = gbageLabDtoRestTemplate;
        gbconListGlobal = gbconList;
        // Dialog theming
        getElement().getThemeList().add("my-dialog");
        setWidth("800px");

        // Accessibility
        getElement().setAttribute("aria-labelledby", "dialog-title");

        // Header
        H2 title = new H2("Formulario Beneficiarios");
        title.addClassName("dialog-title");

        min = new Button(VaadinIcon.DOWNLOAD_ALT.create());
        min.addClickListener(event -> minimise());

        max = new Button(VaadinIcon.EXPAND_SQUARE.create());
        max.addClickListener(event -> maximise());

        //////////////////

        setFinalBeneficiaryList(beneficiary);

        Button close = new Button(VaadinIcon.CLOSE_SMALL.create());
        close.addClickListener(event -> close());

        header = new Header(title, min, max, close);
        header.getElement().getThemeList().add(Lumo.DARK);
        add(header);

        // Content
        HorizontalLayout flex = new HorizontalLayout();
        
        Button btnSearch = new Button("Añadir");
        btnSearch.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnSearch.setIcon(VaadinIcon.SEARCH.create());

        TextField search = new TextField();
        search.setPlaceholder("Ingrese carnet/nit completo");

        radioSearch.setItems("Carnet", "Codigo Agenda");
        radioSearch.setHelperText("Seleccione criterio busqueda");
        radioSearch.setValue("Codigo Agenda");

        flex.add(radioSearch,search,btnSearch);
        flex.setAlignItems(FlexComponent.Alignment.START);
        flex.setSpacing(true);

        btnSearch.addClickListener(event -> {
            Footer footer = new Footer();
            Beneficiary newItem = new Beneficiary();

            resultSearch(radioSearch.getValue(),search.getValue());
            Button save = new Button("Guardar");

            if(gbageLabDtoList.size()==0){
                beneficiaryRegisterView = new BeneficiaryRegisterView(newItem, gbconListGlobal);

                save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                footer.add(save);
                beneficiaryRegisterView.add(footer);
                beneficiaryRegisterView.open();
                save.addClickListener(e -> {
                    if(beneficiaryRegisterView.save()) {
                        beneficiaryRegisterView.close();
                        beneficiaryList.add(beneficiaryRegisterView.beneficiaryGlobal);
                        grid.setItems(beneficiaryList);
                    }
                });

            }else if(gbageLabDtoList.size()==1){
                newItem.setFullName(gbageLabDtoList.get(0).getGbagenomb());
                newItem.setIdCard(gbageLabDtoList.get(0).getGbagendid());
                newItem.setTelephone(gbageLabDtoList.get(0).getGbagetlfd());
                newItem.setEconomicActivity(gbageLabDtoList.get(0).getGbcaedesc());
                newItem.setNationality(gbageLabDtoList.get(0).getGbagenaci());
                newItem.setAddress(gbageLabDtoList.get(0).getGbagedir1());

                save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                footer.add(save);
                beneficiaryRegisterView = new BeneficiaryRegisterView(newItem,gbconListGlobal);
                beneficiaryRegisterView.add(footer);
                beneficiaryRegisterView.open();
                save.addClickListener(e -> {
                    if(beneficiaryRegisterView.save()) {
                        beneficiaryRegisterView.close();
                        beneficiaryList.add(beneficiaryRegisterView.beneficiaryGlobal);
                        grid.setItems(beneficiaryList);
                    }
                });

            }else{
                //Resultado de busqueda
                gridGbageLabDto();
                beneficiarySearch = new BeneficiarySearch(grid2);
                beneficiarySearch.open();

            }

        });

        beneficiaryList = getBeneficiaryList(beneficiary);

        gridBeneficiary();
        content = new VerticalLayout(flex,grid);
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

    @SneakyThrows
    public String getFinalBeneficiaryList(){
        ObjectMapper mapper = new ObjectMapper();
        String result = mapper.writeValueAsString(beneficiaryList);
        return result;
    }

    @SneakyThrows
    private void setFinalBeneficiaryList(String beneficiary){
        ObjectMapper mapper = new ObjectMapper();
        beneficiary = (beneficiary==null || beneficiary=="") ?"[]":beneficiary;
        beneficiaryList = mapper.readValue(beneficiary,new TypeReference<List<Beneficiary>>(){});
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

        btnDelete.addClickListener(event -> {
            List<Beneficiary> b = beneficiaryList
                    .stream()
                    .filter(e -> !e.getId().equals(beneficiary.getId()))
                    .collect(Collectors.toList());
            beneficiaryList = b;
            grid.setItems(beneficiaryList);
        });

        btnEdit.addClickListener(event -> {
            Footer footer = new Footer();
            Button save = new Button("Guardar");
            save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            footer.add(save);
            beneficiaryRegisterView = new BeneficiaryRegisterView(beneficiary,gbconListGlobal);

            beneficiaryRegisterView.add(footer);
            beneficiaryRegisterView.open();
            save.addClickListener(e -> {
                if(beneficiaryRegisterView.save()) {
                    beneficiaryRegisterView.close();

                    List<Beneficiary> b = beneficiaryList
                            .stream()
                            .filter(ev -> !ev.getId().equals(beneficiaryRegisterView.beneficiaryGlobal.getId()))
                            .collect(Collectors.toList());
                    beneficiaryList = b;

                    beneficiaryList.add(beneficiaryRegisterView.beneficiaryGlobal);
                    grid.setItems(beneficiaryList);
                }
            });

        });

        HorizontalLayout layout = new HorizontalLayout();
        layout.add(btnEdit,btnDelete);
        return layout;

    }
///////////

    public void gridGbageLabDto(){
        grid2 = new Grid();
        grid2.setItems(gbageLabDtoList);
        grid2.addColumn(GbageLabDto::getGbagendid)
                .setFlexGrow(1)
                .setAutoWidth(true)
                .setHeader("Carnet");
        grid2.addColumn(GbageLabDto::getGbagenomb)
                .setFlexGrow(1)
                .setAutoWidth(true)
                .setHeader("Nombre completo");
        grid2.addColumn(GbageLabDto::getGblabdact)
                .setFlexGrow(1)
                .setAutoWidth(true)
                .setHeader("Actividad");
        grid2.addColumn(new ComponentRenderer<>(this::createButtonSelect))
                .setFlexGrow(0).setAutoWidth(true);

    }

    private Component createButtonSelect(GbageLabDto gbageLabDto){
        Button btnSelect = new Button();
        btnSelect.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        btnSelect.setIcon(VaadinIcon.CHECK.create());
        btnSelect.addClickListener(event ->{
            Beneficiary newItem = new Beneficiary();
            newItem.setFullName(gbageLabDtoList.get(0).getGbagenomb());
            newItem.setIdCard(gbageLabDtoList.get(0).getGbagendid());
            newItem.setTelephone(gbageLabDtoList.get(0).getGbagetlfd());
            newItem.setEconomicActivity(gbageLabDtoList.get(0).getGblabdact());
            newItem.setNationality(gbageLabDtoList.get(0).getGbagenaci());
            newItem.setAddress(gbageLabDtoList.get(0).getGbagedir1());
            beneficiaryRegisterView = new BeneficiaryRegisterView(newItem,gbconListGlobal);
            Footer footer = new Footer();
            Button save = new Button("Guardar");
            footer.add(save);
            beneficiaryRegisterView.add(footer);
            beneficiaryRegisterView.open();
            save.addClickListener(e -> {
                if(beneficiaryRegisterView.save()) {
                    beneficiaryRegisterView.close();
                    beneficiaryList.add(beneficiaryRegisterView.beneficiaryGlobal);
                    grid.setItems(beneficiaryList);
                }
            });
            beneficiarySearch.close();

        });

        return btnSelect;
    }

    private void resultSearch(String categoria, String searchBy){
//        List<GbageLabDto> gbageLabDtoList = new ArrayList<>();
        if(categoria.equals("Carnet")) {
            searchBy = searchBy.concat("%");
            gbageLabDtoList = gbageLabDtoRestTemplateGlobal.findGbageLabDtoByIdCard(searchBy);
        }else{
            gbageLabDtoList = gbageLabDtoRestTemplateGlobal.findGbageLabDtoByCage(searchBy);
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
