package com.mindware.ui.views.forms;

import com.mindware.backend.entity.Parameter;
import com.mindware.backend.entity.dto.FormToSelectReportDto;
import com.mindware.backend.entity.netbank.dto.DataFormDto;
import com.mindware.backend.entity.netbank.dto.GbageDto;
import com.mindware.backend.rest.contract.ContractRestTemplate;
import com.mindware.backend.rest.forms.FormsRestTemplate;
import com.mindware.backend.rest.netbank.GbageDtoRestTemplate;
import com.mindware.backend.rest.netbank.GbageLabDtoRestTemplate;
import com.mindware.backend.rest.netbank.GbconRestTemplate;
import com.mindware.backend.rest.parameter.ParameterRestTemplate;
import com.mindware.ui.MainLayout;
import com.mindware.ui.components.FlexBoxLayout;
import com.mindware.ui.layout.size.Horizontal;
import com.mindware.ui.layout.size.Top;
import com.mindware.ui.util.DownloadLink;
import com.mindware.ui.util.UIUtils;
import com.mindware.ui.util.css.BoxSizing;
import com.mindware.ui.util.css.Shadow;
import com.mindware.ui.views.SplitViewFrame;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
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
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;
import dev.mett.vaadin.tooltip.Tooltips;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.olli.FileDownloadWrapper;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
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

    @Autowired
    private GbageLabDtoRestTemplate gbageLabDtoRestTemplate;

    @Autowired
    private ParameterRestTemplate parameterRestTemplate;

    @Autowired
    private GbconRestTemplate gbconRestTemplate;

    @Autowired
    private ContractRestTemplate contractRestTemplate;

    private List<GbageDto> gbageDtoList = new ArrayList<>();

    private DialogFormSavingBank dialogFormSavingBank;
    private DialogDigitalBanking dialogDigitalBanking;
    private DialogServiceDebitCard dialogServiceDebitCard;

    private DialogFormToSelectReport dialogFormToSelectReport;

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

        Button btnSelectReports = new Button("Grupo Reportes");
        btnSelectReports.setEnabled(false);
        btnSelectReports.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
        btnSelectReports.setIcon(VaadinIcon.LIST_OL.create());
        Tooltips.getCurrent().setTooltip(btnSelectReports,"Seleccionar reportes a imprimir");

        btnSearch.addClickListener(click -> {
            if (radioSearch.getValue().equals("Carnet")){
                gbageDtoList = gbageDtoRestTemplate.findGbageDto("cardnumber",textToSearch.getValue()+'%');

            }else{
                gbageDtoList = gbageDtoRestTemplate.findGbageDto("cage",textToSearch.getValue());

            }
            if(gbageDtoList.size()>0){
                btnSelectReports.setEnabled(true);
            }else{
                btnSelectReports.setEnabled(false);
            }
            setViewContent(createContent());
        });

        textToSearch.getElement().addEventListener("keyup", e->{
            btnSearch.click();
        }).addEventData("element.value").setFilter("event.keyCode == 13");

//        UI.getCurrent().addShortcutListener(
//                () -> btnSearch.click(), Key.ENTER);


        btnSelectReports.addClickListener(event -> {
            List<FormToSelectReportDto> formToSelectReportDtoList = formsRestTemplate.findFormSelectReportByIdclient(gbageDtoList.get(0).getGbagecage());
            dialogFormToSelectReport = new DialogFormToSelectReport(formToSelectReportDtoList,formsRestTemplate);
            dialogFormToSelectReport.open();
        });

        panelSearch.add(radioSearch, textToSearch, btnSearch, btnSelectReports);
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
        grid.addThemeVariants(GridVariant.LUMO_COMPACT,GridVariant.LUMO_WRAP_CELL_CONTENT);
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
                .setHeader("Número cuenta")
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
//        grid.addColumn(new ComponentRenderer<>(this::createSelectReport))
//                        .setFlexGrow(0).setAutoWidth(true);
        grid.addColumn(new ComponentRenderer<>(this::createDownloadLink))
                .setFlexGrow(0).setAutoWidth(true);
        return grid;
    }


    private Component createDownloadLink(GbageDto gbageDto){
        Div content = new Div();

        if(gbageDto.getAccountName()!=null && (gbageDto.getAccountName().equals("CAJA-AHORRO") || gbageDto.getAccountName().equals("DPF"))){

            String nameTemplate = null;
            try {
                int year = getYears(gbageDto);
                if(year < 18) {
                    nameTemplate = contractRestTemplate.getTemplateContract(gbageDto.getGbagecage(), gbageDto.getAccountCode(),
                            "CONTRATO", gbageDto.getAccountName(), "NO", gbageDto.getTypeAccount(),"SI");
                }else{
                    nameTemplate = contractRestTemplate.getTemplateContract(gbageDto.getGbagecage(), gbageDto.getAccountCode(),
                            "CONTRATO", gbageDto.getAccountName(), "NO", gbageDto.getTypeAccount(),"NO");
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            if(!nameTemplate.equals("invalid")) {
                File in = new File(nameTemplate);
                DownloadLink downloadLink = new DownloadLink(in);
                content = new Div(downloadLink);
            }


        }

        return content;
    }

    private Component createForm(GbageDto gbageDto) {
        Button btnTask = new Button("Crear");
        btnTask.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);
        btnTask.setIcon(VaadinIcon.FILE_ADD.create());

        Button btnPrint = new Button();
        btnPrint.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_CONTRAST);
        btnPrint.setIcon(VaadinIcon.PRINT.create());

//        Button btnDownloadTemplate = new Button();
//        btnDownloadTemplate.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
//        btnDownloadTemplate.setIcon(VaadinIcon.DOWNLOAD_ALT.create());

        Select<String> taskSelect = new Select<>();
        if(gbageDto.getAccountName().equals("VARIOS")){
            taskSelect.setItems("BANCA DIGITAL", "SERVICIOS TD" );
            taskSelect.setPlaceholder("Seleccionar Tarea");
            btnPrint.setVisible(false);
        }else {
            taskSelect.setItems("FORMULARIO APERTURA", "CONTRATO");
            taskSelect.setPlaceholder("Seleccione Tarea");
            btnPrint.setVisible(true);
        }

        btnTask.addClickListener(click -> {
            if(!taskSelect.isEmpty()) {
                if (gbageDto.getAccountName().equals("VARIOS")) {

                    if(taskSelect.getValue().equals("BANCA DIGITAL")){
                        openDialog(gbageDto.getGbagecage(),"",  gbageDto.getAccountName(), taskSelect.getValue(),"NO");
                    }
                    if(taskSelect.getValue().equals("SERVICIOS TD")){
                        openDialog(gbageDto.getGbagecage(),"",  gbageDto.getAccountName(), taskSelect.getValue(),"NO");
                    }

                } else {

                    if (gbageDto.getAccountName().equals("CAJA-AHORRO") ) {
                        if(taskSelect.getValue().equals("CONTRATO")){

                        }else {
                            int years = getYears(gbageDto);
                            if(years < 18){
                                openDialog(gbageDto.getSecundaryCage(), gbageDto.getAccountCode(), gbageDto.getAccountName(), taskSelect.getValue(),"SI");
                            }else{
                                if(gbageDto.getSecundaryCage().equals(gbageDto.getGbagecage())){
                                    openDialog(gbageDto.getGbagecage(), gbageDto.getAccountCode(), gbageDto.getAccountName(), taskSelect.getValue(),"NO");
                                }else {
                                    openDialog(gbageDto.getSecundaryCage(), gbageDto.getAccountCode(), gbageDto.getAccountName(), taskSelect.getValue(), "SI");
                                }
                            }

                        }
                    }else if (gbageDto.getAccountName().equals("DPF") && !taskSelect.getValue().equals("CONTRATO")){
                        openDialog(gbageDto.getGbagecage(), gbageDto.getAccountCode(), gbageDto.getAccountName(), taskSelect.getValue(),"NO");
                    }

                }
            }else{
                UIUtils.dialog("Seleccione una tarea","info").open();
            }
        });

        btnPrint.addClickListener(click ->{
           if( taskSelect.getValue()!=null && !taskSelect.getValue().isEmpty()) {
               if(taskSelect.getValue().equals("FORMULARIO APERTURA")) {
                   try {
                       FormReportView report=null;
                       if(gbageDto.getAccountName().equals("CAJA-AHORRO")) {
                           int years = getYears(gbageDto);
                           if(years < 18){
                               report = new FormReportView(gbageDto.getSecundaryCage(), gbageDto.getAccountCode(),
                                       taskSelect.getValue(), gbageDto.getAccountName(),
                                       formsRestTemplate, "", "", contractRestTemplate, "SI","SI");
                           }else{
                               if(gbageDto.getSecundaryCage().equals(gbageDto.getGbagecage())) {
                                   report = new FormReportView(gbageDto.getGbagecage(), gbageDto.getAccountCode(),
                                           taskSelect.getValue(), gbageDto.getAccountName(),
                                           formsRestTemplate, "", "", contractRestTemplate, "NO","NO");
                               }else{
                                   report = new FormReportView(gbageDto.getSecundaryCage(), gbageDto.getAccountCode(),
                                           taskSelect.getValue(), gbageDto.getAccountName(),
                                           formsRestTemplate, "", "", contractRestTemplate, "SI","NO");
                               }
                           }

                       }else{
                           report = new FormReportView(gbageDto.getGbagecage(), gbageDto.getAccountCode(),
                                   taskSelect.getValue(), gbageDto.getAccountName(),
                                   formsRestTemplate, "", "",contractRestTemplate,"NO","NO");
                       }
                       report.open();
                   } catch (Exception e) {

                   }
               }else if(taskSelect.getValue().equals("CONTRATO")){
                   FormReportView report = null;
                   String login = VaadinSession.getCurrent().getAttribute("login").toString();
                   try {
                       if(gbageDto.getAccountName().equals("CAJA-AHORRO")) {
                           int years = getYears(gbageDto);
                           if(years < 18){
                               report = new FormReportView(gbageDto.getSecundaryCage(), gbageDto.getAccountCode(),
                                       taskSelect.getValue(), gbageDto.getAccountName(), formsRestTemplate, gbageDto.getTypeAccount().trim(), login, contractRestTemplate, "SI","SI");
                           }else{
                               report = new FormReportView(gbageDto.getGbagecage(), gbageDto.getAccountCode(),
                                       taskSelect.getValue(), gbageDto.getAccountName(), formsRestTemplate, gbageDto.getTypeAccount().trim(), login, contractRestTemplate, "NO","NO");
                           }
                       }else {
                           report = new FormReportView(gbageDto.getGbagecage(), gbageDto.getAccountCode(),
                                   taskSelect.getValue(), gbageDto.getAccountName(), formsRestTemplate, "", login, contractRestTemplate, "NO","NO");
                       }
                       report.open();
                   }catch (Exception e){

                   }
               }

           }else{
               UIUtils.dialog("Seleccione una tarea","info").open();
               return;
           }
        });

//        if(gbageDto.getAccountCode()!=null && !gbageDto.getAccountCode().equals("")) {
//            String nameTemplate = null;
//            try {
//                nameTemplate = contractRestTemplate.getTemplateContract(gbageDto.getGbagecage(), gbageDto.getAccountCode(),
//                        taskSelect.getValue(), gbageDto.getAccountName(), "NO", gbageDto.getTypeAccount());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            File in = new File(nameTemplate);
//
//            StreamResource sr = new StreamResource(in.getName(), () -> new ByteArrayInputStream("template".getBytes()));
//            FileDownloadWrapper buttonWrapper = new FileDownloadWrapper(sr);
//            buttonWrapper.wrapComponent(btnDownloadTemplate);
//        }
//        btnDownloadTemplate.addClickListener(event -> {
//
//
//        });

        HorizontalLayout layout = new HorizontalLayout();
        layout.add(taskSelect,btnTask,btnPrint);

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

    private void openDialog(Integer cage, String accountCode, String categoryTypeForm, String nameTypeForm, String isTutor){
        if(dialogFormSavingBank != null){
            dialogFormSavingBank.close();
        }

        if(categoryTypeForm.equals("CAJA-AHORRO") || categoryTypeForm.equals("DPF")) {
            DataFormDto dataFormDto = formsRestTemplate.findDataFormDtoFormSavingBoxByCageAndAccount(cage,
                    accountCode,categoryTypeForm,isTutor);
            dialogFormSavingBank = new DialogFormSavingBank(accountCode, categoryTypeForm, nameTypeForm,
                    dataFormDto, formsRestTemplate, gbageLabDtoRestTemplate, gbconRestTemplate);
            dialogFormSavingBank.open();
        }else if(categoryTypeForm.equals("VARIOS") && nameTypeForm.equals("BANCA DIGITAL")){
            try {
                List<Parameter> parameterList = parameterRestTemplate.findAllActive();

                List<DataFormDto> dataFormDto = formsRestTemplate.findDataFormForDigitalBank(cage);
                dialogDigitalBanking = new DialogDigitalBanking(dataFormDto, parameterList, formsRestTemplate);
                dialogDigitalBanking.open();
            }catch (Exception e) {
                UIUtils.dialog(e.getMessage(),"alert").open();
            }
        }else if(categoryTypeForm.equals("VARIOS") && nameTypeForm.equals("SERVICIOS TD")){
            try {
                List<Parameter> parameterList = parameterRestTemplate.findAllActive();

                List<DataFormDto> dataFormDto = formsRestTemplate.findDataFormForDigitalBank(cage);
                dialogServiceDebitCard = new DialogServiceDebitCard(dataFormDto, parameterList, formsRestTemplate);
                dialogServiceDebitCard.open();
            }catch (Exception e) {
                UIUtils.dialog(e.getMessage(),"alert").open();
            }
        }

    }


}
