package com.mindware.ui.views.forms;

import com.mindware.backend.entity.netbank.dto.DataFormDto;
import com.mindware.backend.entity.netbank.dto.GbageDto;
import com.mindware.backend.rest.forms.FormsRestTemplate;
import com.mindware.backend.rest.netbank.GbageDtoRestTemplate;
import com.mindware.ui.MainLayout;
import com.mindware.ui.components.FlexBoxLayout;
import com.mindware.ui.layout.size.Horizontal;
import com.mindware.ui.layout.size.Top;
import com.mindware.ui.util.UIUtils;
import com.mindware.ui.util.css.BoxSizing;
import com.mindware.ui.util.css.Shadow;
import com.mindware.ui.views.SplitViewFrame;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Route(value = "Formulario-Busqueda", layout = MainLayout.class)
@ParentLayout(MainLayout.class)
@PageTitle("Formulario de busqueda")
public class FormSearchView extends SplitViewFrame implements RouterLayout {

    @Autowired
    private GbageDtoRestTemplate gbageDtoRestTemplate;

    @Autowired
    private FormsRestTemplate formsRestTemplate;

    private List<GbageDto> gbageDtoList = new ArrayList<>();

    DialogFormSavingBank dialogFormSavingBank;

    @Override
    protected void onAttach(AttachEvent attachment){
        super.onAttach(attachment);

        setViewHeader(createTopBar());


    }

    private Component createContent(){
        FlexBoxLayout content = new FlexBoxLayout(createGridResult());
        content.addClassName("grid-view");
        content.setBoxSizing(BoxSizing.BORDER_BOX);
        content.setHeightFull();
        content.setPadding(Horizontal.RESPONSIVE_X, Top.RESPONSIVE_X);
        return content;
    }

    private HorizontalLayout createTopBar(){
        HorizontalLayout panelSearch = new HorizontalLayout();

        RadioButtonGroup<String> radioSearch = new RadioButtonGroup<>();
        radioSearch.setItems("Carnet", "Codigo Agenda");
        radioSearch.setHelperText("Seleccione criterio busqueda");
        radioSearch.setValue("Codigo Agenda");

        TextField textToSearch = new TextField();
        textToSearch.setValueChangeMode(ValueChangeMode.EAGER);

        Button btnSearch = new Button("Buscar", new Icon(VaadinIcon.SEARCH));

        btnSearch.addClickListener(click -> {
            if (radioSearch.getValue().equals("Carnet")){
                gbageDtoList = gbageDtoRestTemplate.findGbageDto("cardnumber",textToSearch.getValue()+'%');

            }else{
                gbageDtoList = gbageDtoRestTemplate.findGbageDto("cage",textToSearch.getValue());

            }
            setViewContent(createContent());
        });

        UI.getCurrent().addShortcutListener(
                () -> btnSearch.click(), Key.ENTER);

        panelSearch.add(radioSearch, textToSearch, btnSearch);
        panelSearch.setSpacing(true);
        panelSearch.setAlignItems(FlexComponent.Alignment.START);
        panelSearch.setPadding(true);

        UIUtils.setShadow(Shadow.S,panelSearch);

        return panelSearch;
    }

    private void executeSearch(String criteria, String find){
        if (criteria.equals("Carnet")){
            gbageDtoList = gbageDtoRestTemplate.findGbageDto("cardnumber",find.concat("%"));

        }else{
            gbageDtoList = gbageDtoRestTemplate.findGbageDto("cage",find);

        }
    }

    private Grid createGridResult(){
        Grid<GbageDto> grid = new Grid<>();
        grid.setSizeFull();
        grid.setItems(gbageDtoList);

        grid.addColumn(GbageDto::getGbagecage)
                .setHeader("Código agenda")
                .setFlexGrow(0)
                .setSortable(true)
                .setResizable(true)
                .setAutoWidth(true);
        grid.addColumn(GbageDto::getGbagendid)
                .setHeader("Carnet")
                .setFlexGrow(0)
                .setSortable(true)
                .setResizable(true)
                .setAutoWidth(true);
        grid.addColumn(GbageDto::getGbagenomb)
                .setHeader("Nombre completo")
                .setFlexGrow(0)
                .setSortable(true)
                .setResizable(true)
                .setAutoWidth(true);
        grid.addColumn(new LocalDateRenderer<>(GbageDto::getGbagefregConvert, DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .setHeader("Fecha registro")
                .setFlexGrow(0)
                .setSortable(true)
                .setResizable(true)
                .setAutoWidth(true);
        grid.addColumn(new LocalDateRenderer<>(GbageDto::getOpeningDateConvert, DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .setHeader("Apertura cuenta")
                .setFlexGrow(0)
                .setSortable(true)
                .setResizable(true)
                .setAutoWidth(true);
        grid.addColumn(GbageDto::getAccountName)
                .setHeader("Tipo cuenta")
                .setFlexGrow(0)
                .setSortable(true)
                .setResizable(true)
                .setAutoWidth(true);
        grid.addColumn(GbageDto::getAccountCode)
                .setHeader("Numero cuenta")
                .setFlexGrow(0)
                .setSortable(true)
                .setResizable(true)
                .setAutoWidth(true);
        grid.addColumn(GbageDto::getCurrency)
                .setHeader("Moneda")
                .setFlexGrow(0)
                .setSortable(true)
                .setResizable(true)
                .setAutoWidth(true);
        grid.addColumn(new ComponentRenderer<>(this::createForm))
                .setFlexGrow(0).setAutoWidth(true);

        return grid;
    }

    private Component createForm(GbageDto gbageDto){
        Button btnTask = new Button("Crear");
        btnTask.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);
        btnTask.setIcon(VaadinIcon.FILE_ADD.create());

        Button btnPrint = new Button();
        btnPrint.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_CONTRAST);
        btnPrint.setIcon(VaadinIcon.PRINT.create());

        Select<String> taskSelect = new Select<>();
        if(gbageDto.getAccountName().equals("VARIOS")){
            taskSelect.setItems("BANCA DIGITAL", "ENTREGA TD","SERVICIOS TD" );
            taskSelect.setPlaceholder("Seleccionar Tarea");
        }else {
            taskSelect.setItems("FORMULARIO APERTURA", "CONTRATO");
            taskSelect.setPlaceholder("Seleccione Tarea");
        }

        btnTask.addClickListener(click -> {
            if(!taskSelect.isEmpty()) {
                if (gbageDto.getAccountName().equals("VARIOS")) {
                    UIUtils.showNotification("TARJETA DBITO");
                } else {
                    if (gbageDto.getAccountName().equals("CAJA-AHORRO") || gbageDto.getAccountName().equals("DPF")) {

                        openDialog(gbageDto.getGbagecage(), gbageDto.getAccountCode(), gbageDto.getAccountName(), taskSelect.getValue());
                    }
                }
            }else{
                UIUtils.showNotification("Seleccione una tarea a realizar");
            }
        });

        HorizontalLayout layout = new HorizontalLayout();
        layout.add(taskSelect,btnTask,btnPrint);

        return layout;
    }

    private void openDialog(Integer cage, String accountCode, String categoryTypeForm, String nameTypeForm){
        if(dialogFormSavingBank != null){
            dialogFormSavingBank.close();
        }

        if(categoryTypeForm.equals("CAJA-AHORRO") || categoryTypeForm.equals("DPF")) {
            DataFormDto dataFormDto = formsRestTemplate.findDataFormDtoFormSavingBoxByCageAndAccount(cage, accountCode,categoryTypeForm);
            dialogFormSavingBank = new DialogFormSavingBank(accountCode, categoryTypeForm, nameTypeForm, dataFormDto, formsRestTemplate);
        }
        dialogFormSavingBank.open();
    }

}
