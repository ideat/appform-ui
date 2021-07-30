package com.mindware.ui.views.parameter;

import com.mindware.backend.entity.Parameter;
import com.mindware.backend.rest.parameter.ParameterRestTemplate;
import com.mindware.backend.util.GrantOptions;
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
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

@Route(value = "parameter", layout = MainLayout.class)
@ParentLayout(MainLayout.class)
@PageTitle("Parametros")
public class ParameterView extends SplitViewFrame implements RouterLayout {

    @Autowired
    private ParameterRestTemplate restTemplate;

    private Grid<Parameter> grid;

    private DetailsDrawer detailsDrawer;
    private DetailsDrawerHeader detailsDrawerHeader;
    private Button btnNew;

    private DetailsDrawerFooter footer;

    private Binder<Parameter> binder;

    private Parameter current;

    private ListDataProvider<Parameter> dataProvider;

    private List<Parameter> parameterList = new ArrayList<>();

    private ComboBox<String> cmbCategoryFilter;

    private TextField txtValueFilter;

    private TextField txtNameFilter;

    private String[] param = { "BANCA DIGITAL, SERVICIOS", "BANCA DIGITAL, OPERACIONES", "TARJETA DEBITO, SERVICIOS","LIMITES BANCA DIGITAL"};

    @Override
    protected void onAttach(AttachEvent attachEvent){
        super.onAttach(attachEvent);
        getListParameter();
        setViewHeader(createTopBar());
        setViewContent(createContent());
        setViewDetails(createDetailsDrawer());
    }

    private Component createContent(){
        FlexBoxLayout content = new FlexBoxLayout(createGridParameter());
        content.addClassName("grid-view");
        content.setHeightFull();
        content.setBoxSizing(BoxSizing.BORDER_BOX);
        content.setPadding(Horizontal.RESPONSIVE_X, Top.RESPONSIVE_X);
        return content;
    }

    private void getListParameter(){
//        String paramStr = Arrays.asList(param).stream().collect(Collectors.joining(","));
        parameterList = new ArrayList<>(restTemplate.findAll());
        dataProvider = new ListDataProvider<>(parameterList);
    }

    private HorizontalLayout createTopBar(){
        btnNew = new Button("Nuevo parametro");
        btnNew.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnNew.setIcon(VaadinIcon.PLUS_CIRCLE.create());
        btnNew.addClickShortcut(Key.KEY_N, KeyModifier.ALT);
//        btnNew.setEnabled(GrantOptions.grantedOption("Parametros"));
        btnNew.addClickListener(e -> {
            showDetails(new Parameter());
        });

        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setWidth("100%");
        topLayout.add(btnNew);
        topLayout.setVerticalComponentAlignment(FlexComponent.Alignment.END,btnNew);
        topLayout.setSpacing(true);
        topLayout.setPadding(true);

        return topLayout;
    }

    private Grid createGridParameter(){

        grid = new Grid<>();
        grid.setId("parameter");
        grid.setMultiSort(true);
        grid.setHeightFull();
        grid.setWidthFull();

        grid.addSelectionListener(event -> event.getFirstSelectedItem()
                .ifPresent(this::showDetails));

        grid.setDataProvider(dataProvider);

        grid.addColumn(Parameter::getCategory).setFlexGrow(1).setFrozen(false)
                .setHeader("Categoria").setSortable(true).setKey("category").setResizable(true)
                .setAutoWidth(true).setTextAlign(ColumnTextAlign.START);
        grid.addColumn(Parameter::getName).setFlexGrow(1).setKey("name")
                .setHeader("Nombre variable").setAutoWidth(true).setResizable(true)
                .setTextAlign(ColumnTextAlign.START);
        grid.addColumn(Parameter::getValue).setFlexGrow(1).setKey("value")
                .setHeader("Valor").setSortable(true).setFrozen(false).setResizable(true)
                .setAutoWidth(true).setTextAlign(ColumnTextAlign.START);
        grid.addColumn(Parameter::getState).setFlexGrow(1).setKey("value")
                .setHeader("Estado").setSortable(true).setFrozen(false).setResizable(true)
                .setAutoWidth(true).setTextAlign(ColumnTextAlign.START);

        HeaderRow hr = grid.appendHeaderRow();

        cmbCategoryFilter = new ComboBox<>();
        cmbCategoryFilter.setItems(param);
        cmbCategoryFilter.setWidth("100%");
        cmbCategoryFilter.addValueChangeListener(e ->{
            applyFilter(dataProvider);
        });
        hr.getCell(grid.getColumnByKey("category")).setComponent(cmbCategoryFilter);

        txtNameFilter = new TextField();
        txtNameFilter.setValueChangeMode(ValueChangeMode.EAGER);
        txtNameFilter.setWidth("100%");
        txtNameFilter.addValueChangeListener(e ->{

            applyFilter(dataProvider);
        });
        hr.getCell(grid.getColumnByKey("name")).setComponent(txtNameFilter);

        txtValueFilter = new TextField();
        txtValueFilter.setValueChangeMode(ValueChangeMode.EAGER);
        txtValueFilter.setWidth("100%");
        txtValueFilter.addValueChangeListener(e ->{
            applyFilter(dataProvider);
        });
        hr.getCell(grid.getColumnByKey("value")).setComponent(txtValueFilter);

        return grid;
    }

    private DetailsDrawer createDetailsDrawer(){
        detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.RIGHT);

        // Header
        detailsDrawerHeader = new DetailsDrawerHeader("");
        detailsDrawerHeader.addCloseListener(buttonClickEvent -> detailsDrawer.hide());
        detailsDrawer.setHeader(detailsDrawerHeader);

        footer = new DetailsDrawerFooter();
        footer.addSaveListener(e ->{
            if (current !=null && binder.writeBeanIfValid(current)){
                Parameter result = restTemplate.create(current);
                if (current.getId()==null){
                    parameterList.add(result);
                    grid.getDataProvider().refreshAll();
                }else{
                    grid.getDataProvider().refreshItem(current);
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

    private void showDetails(Parameter parameter){
        current = parameter;
        detailsDrawerHeader.setTitle("Parametro: "+ parameter.getValue());
        detailsDrawer.setContent(createDetails(parameter));
        detailsDrawer.show();

    }

    private FormLayout createDetails(Parameter parameter){

        ComboBox<String> cmbCategory = new ComboBox<>();
        cmbCategory.setItems(param);
        cmbCategory.setValue(Optional.ofNullable(parameter.getCategory()).orElse(""));
        cmbCategory.setWidth("100%");
        cmbCategory.setRequired(true);

        TextField txtName = new TextField();
        txtName.setValue(Optional.ofNullable(parameter.getName()).orElse(""));
        txtName.setRequired(true);
        txtName.setWidth("100%");

        TextArea txtValue = new TextArea();
        txtValue.setValue(Optional.ofNullable(parameter.getValue()).orElse(""));
        txtValue.setRequired(true);
        txtValue.setWidth("100%");

        ComboBox<String> state = new ComboBox<>();
        state.setItems("ACTIVO","BAJA");
        state.setWidthFull();
        state.setRequired(true);

        binder = new BeanValidationBinder<>(Parameter.class);
        binder.forField(cmbCategory).asRequired("Categoria es requerida").bind(Parameter::getCategory,Parameter::setCategory);
        binder.forField(txtValue).asRequired("Valor es requerido").bind(Parameter::getValue,Parameter::setValue);
        binder.forField(txtName).asRequired("Descripcion es requerida").bind(Parameter::getName,Parameter::setName);
        binder.forField(state).asRequired("Estado es requerido").bind(Parameter::getState, Parameter::setState);
        binder.addStatusChangeListener(event ->{
            boolean isValid = !event.hasValidationErrors();
            boolean hasChanges = binder.hasChanges();
            footer.saveState(hasChanges && isValid); //&& GrantOptions.grantedOption("Parametros"));
        });

        FormLayout form = new FormLayout();
        form.addClassNames(LumoStyles.Padding.Bottom.L,
                LumoStyles.Padding.Horizontal.S, LumoStyles.Padding.Top.S);
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("21em", 2,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP));

        FormLayout.FormItem categoryItem = form.addFormItem(cmbCategory,"Categoria");
        UIUtils.setColSpan(2,categoryItem);
        FormLayout.FormItem descriptionItem = form.addFormItem(txtName,"Nombre");
        UIUtils.setColSpan(2,descriptionItem);
        FormLayout.FormItem valorItem =  form.addFormItem(txtValue,"Valor");
        UIUtils.setColSpan(2,valorItem);

        return form;

    }

    private void applyFilter(ListDataProvider<Parameter> dataProvider){
        dataProvider.clearFilters();
        if (cmbCategoryFilter.getValue()!=null){
            dataProvider.addFilter(parameter -> Objects.equals(cmbCategoryFilter.getValue(),parameter.getCategory()));
        }
        if(!txtValueFilter.getValue().trim().equals("")){
//            dataProvider.addFilter(parameter -> Objects.equals(txtValueFilter.getValue(),parameter.getValue()));
            dataProvider.addFilter(parameter -> StringUtils.containsIgnoreCase(parameter.getValue(),txtValueFilter.getValue()));
        }
        if(!txtNameFilter.getValue().trim().equals("")){
//            dataProvider.addFilter(parameter -> Objects.equals(txtDescriptionFilter.getValue(),parameter.getDescription()));
            dataProvider.addFilter(parameter -> StringUtils.containsIgnoreCase(parameter.getName(),txtNameFilter.getValue()));
        }


    }
}
