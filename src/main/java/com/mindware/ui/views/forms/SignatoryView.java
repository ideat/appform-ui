package com.mindware.ui.views.forms;

import com.mindware.backend.entity.Signatory;
import com.mindware.backend.rest.signatory.SignatoryRestTemplate;
import com.mindware.ui.MainLayout;
import com.mindware.ui.components.FlexBoxLayout;
import com.mindware.ui.components.detailsdrawer.DetailsDrawer;
import com.mindware.ui.components.detailsdrawer.DetailsDrawerFooter;
import com.mindware.ui.components.detailsdrawer.DetailsDrawerHeader;
import com.mindware.ui.layout.size.Horizontal;
import com.mindware.ui.layout.size.Top;
import com.mindware.ui.util.LumoStyles;
import com.mindware.ui.util.UIUtils;
import com.mindware.ui.util.css.BoxSizing;
import com.mindware.ui.views.SplitViewFrame;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.LocalDateToDateConverter;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import dev.mett.vaadin.tooltip.Tooltips;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Route(value = "signatory", layout = MainLayout.class)
@ParentLayout(MainLayout.class)
@PageTitle("Lista de Representantes Legales")
public class SignatoryView extends SplitViewFrame implements RouterLayout {

    @Autowired
    private SignatoryRestTemplate signatoryRestTemplate;

    private Grid<Signatory> grid;

    private ListDataProvider<Signatory> dataProvider;

    private List<Signatory> signatoryList;

    private DetailsDrawer detailsDrawer;
    private DetailsDrawerHeader detailsDrawerHeader;
    private Button btnNew;

    private DetailsDrawerFooter footer;
    private Binder<Signatory> binder;
    private Signatory current;

    @Override
    protected void onAttach(AttachEvent attachEvent){
        super.onAttach(attachEvent);
        getListSignatory();
        setViewHeader(createToBar());
        setViewContent(createContent());
        setViewDetails(createDetailsDrawer());
    }

    private Component createContent(){
        FlexBoxLayout content = new FlexBoxLayout(createGridSignatory());
        content.addClassName("grid-view");
        content.setHeightFull();
        content.setBoxSizing(BoxSizing.BORDER_BOX);
        content.setPadding(Horizontal.RESPONSIVE_X, Top.RESPONSIVE_X);
        return content;
    }

    private void getListSignatory(){
        List<Signatory> list = signatoryRestTemplate.findAll();
        signatoryList = new ArrayList<>(list);
        dataProvider = new ListDataProvider<>(signatoryList);
    }

    private HorizontalLayout createToBar(){
        btnNew = new Button("Nuevo");
        btnNew.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnNew.setIcon(VaadinIcon.PLUS_CIRCLE.create());
        btnNew.addClickShortcut(Key.KEY_N, KeyModifier.ALT);
        btnNew.addClickListener(click -> {
           showDetails(new Signatory());

        });

        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setWidth("100%");
        topLayout.add(btnNew);
        topLayout.setVerticalComponentAlignment(FlexComponent.Alignment.END,btnNew);
        topLayout.setSpacing(true);
        topLayout.setPadding(true);

        return topLayout;
    }

    private Grid createGridSignatory(){

        grid = new Grid<>();
        grid.setMultiSort(true);
        grid.setHeightFull();
        grid.setWidthFull();

        grid.setDataProvider(dataProvider);
//        grid.addSelectionListener(event -> event.getFirstSelectedItem()
//                .ifPresent(this::showDetails));

        grid.addColumn(Signatory::getFullName)
                .setFlexGrow(0)
                .setHeader("Representante Legal")
                .setSortable(true)
                .setResizable(true)
                .setAutoWidth(true);
        grid.addColumn(Signatory::getPlaza)
                .setFlexGrow(0)
                .setHeader("Sucursal")
                .setSortable(true)
                .setResizable(true)
                .setAutoWidth(true);
        grid.addColumn(Signatory::getActive)
                .setFlexGrow(0)
                .setHeader("Activo")
                .setSortable(true)
                .setResizable(true)
                .setAutoWidth(true);
        grid.addColumn(new ComponentRenderer<>(this::createEditButton))
                .setFlexGrow(0)
                .setAutoWidth(true);

        return grid;
    }

    private Component createEditButton(Signatory signatory){
        Button button = new Button();
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY,ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_SMALL);
        button.setIcon(VaadinIcon.EDIT.create());
        Tooltips.getCurrent().setTooltip(button,"Editar");
        button.addClickListener(event ->{
            showDetails(signatory);
        });

        return button;
    }

    private DetailsDrawer createDetailsDrawer(){
        detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.RIGHT);

        // Header
        detailsDrawerHeader = new DetailsDrawerHeader("");
        detailsDrawerHeader.addCloseListener(buttonClickEvent -> detailsDrawer.hide());
        detailsDrawer.setHeader(detailsDrawerHeader);

        footer = new DetailsDrawerFooter();

        footer.addSaveListener(e -> {
            if(current !=null & binder.writeBeanIfValid(current)){
                Signatory result = signatoryRestTemplate.create(current);
                if(current.getId() == null){
                    signatoryList.add(result);
                    grid.getDataProvider().refreshAll();
                }else{
                    grid.getDataProvider().refreshAll();
                }
                detailsDrawer.hide();
            }else{
                UIUtils.dialog("Datos incorrectos, verifique nuevamente","alert").open();
            }
        });

        footer.addCancelListener(e ->{
            footer.saveState(false);
            detailsDrawer.hide();
        });

        detailsDrawer.setFooter(footer);
        return detailsDrawer;
    }

    private void showDetails(Signatory signatory){
        current = signatory;
        detailsDrawerHeader.setTitle("Representante Legal: " + signatory.getFullName());
        detailsDrawer.setContent(createDetails(signatory));
        detailsDrawer.show();
    }

    private FormLayout createDetails(Signatory signatory){

        TextField fullName = new TextField();
        fullName.setRequired(true);
        fullName.setWidthFull();
        fullName.setRequiredIndicatorVisible(true);

        TextField idCard = new TextField();
        idCard.setRequired(true);
        idCard.setWidthFull();
        idCard.setRequiredIndicatorVisible(true);

        TextField position = new TextField();
        position.setRequired(true);
        position.setWidthFull();
        position.setRequiredIndicatorVisible(true);

        TextField powerNotary = new TextField();
        powerNotary.setRequired(true);
        powerNotary.setWidthFull();
        powerNotary.setRequiredIndicatorVisible(true);

        DatePicker datePowerNotary = new DatePicker();
        datePowerNotary.setRequired(true);
        datePowerNotary.setWidthFull();
        datePowerNotary.setRequiredIndicatorVisible(true);

        IntegerField numberNotary = new IntegerField();
        numberNotary.setWidthFull();
        numberNotary.setRequiredIndicatorVisible(true);

        TextField notaryName = new TextField();
        notaryName.setRequired(true);
        notaryName.setWidthFull();
        notaryName.setRequiredIndicatorVisible(true);

        IntegerField plaza = new IntegerField();
        plaza.setWidthFull();
        plaza.setRequiredIndicatorVisible(true);

        TextField tradeRegistration = new TextField();
        tradeRegistration.setRequired(true);
        tradeRegistration.setWidthFull();
        tradeRegistration.setRequiredIndicatorVisible(true);

        RadioButtonGroup<String> active = new RadioButtonGroup();
        active.setItems("SI","NO");
        active.setValue("SI");

        binder = new BeanValidationBinder<>(Signatory.class);
        binder.forField(fullName)
                .asRequired("Nombre completo es requerido")
                .bind(Signatory::getFullName,Signatory::setFullName);
        binder.forField(idCard)
                .asRequired("Carnet identidad es querido")
                .bind(Signatory::getIdCard,Signatory::setIdCard);
        binder.forField(position)
                .asRequired("Cargo es requerido")
                .bind(Signatory::getPosition,Signatory::setPosition);
        binder.forField(powerNotary)
                .asRequired("Numero poder es requerido")
                .bind(Signatory::getPowerNotary,Signatory::setPowerNotary);
        binder.forField(datePowerNotary)
                .asRequired("Fecha del poder es requerida")
                .withConverter(new LocalDateToDateConverter())
                .bind(Signatory::getDatePowerNotary,Signatory::setDatePowerNotary);
        binder.forField(numberNotary)
                .asRequired("Numero de notaria es querida")
                .bind(Signatory::getNumberNotary,Signatory::setNumberNotary);
        binder.forField(notaryName)
                .asRequired("Nombre del notario es requerido")
                .bind(Signatory::getNotaryName,Signatory::setNotaryName);
        binder.forField(plaza)
                .asRequired("Codigo de sucursal es requerido")
                .bind(Signatory::getPlaza,Signatory::setPlaza);
        binder.forField(tradeRegistration)
//                .asRequired("Numero de registro de comercio es requerido")
                .bind(Signatory::getTradeRegistration,Signatory::setTradeRegistration);
        binder.forField(active)
                .asRequired("Se debe indicar si el Representante esta activo")
                .bind(Signatory::getActive,Signatory::setActive);
        binder.addStatusChangeListener(event -> {
            boolean isValid = !event.hasValidationErrors();
            boolean hasChanges = binder.hasChanges();
            footer.saveState(hasChanges && isValid);
        });
        binder.readBean(signatory);

        FormLayout form = new FormLayout();
        form.addClassNames(LumoStyles.Padding.Bottom.L,
                LumoStyles.Padding.Horizontal.S, LumoStyles.Padding.Top.S);
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("21em", 2,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP));

        FormLayout.FormItem fullNameItem = form.addFormItem(fullName,"Representante Legal");
        UIUtils.setColSpan(2,fullNameItem);
        form.addFormItem(idCard,"Carnet Identidad");
        form.addFormItem(position, "Cargo");
        form.addFormItem(plaza,"Cod. Sucursal");
        form.addFormItem(powerNotary,"Nro. Poder");
        form.addFormItem(datePowerNotary,"Fecha poder");
        form.addFormItem(numberNotary,"Nro Notaria");
        FormLayout.FormItem notaryNameItem = form.addFormItem(notaryName,"Nombre del notario");
        UIUtils.setColSpan(2,notaryNameItem);
        form.addFormItem(tradeRegistration,"Nro Comercio");
        form.addFormItem(active,"Activo?");

        return form;


    }
}
