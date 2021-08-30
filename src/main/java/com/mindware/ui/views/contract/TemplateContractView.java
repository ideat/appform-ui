package com.mindware.ui.views.contract;

import com.mindware.backend.entity.TemplateContract;
import com.mindware.backend.rest.contract.TemplateContractRestTemplate;
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
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Route(value = "template-contract", layout = MainLayout.class)
@ParentLayout(MainLayout.class)
@PageTitle("Plantillas de Contratos")
public class TemplateContractView extends SplitViewFrame implements RouterLayout {

    @Value("${temp_file}")
    private String tempFile;
    @Autowired
    private TemplateContractRestTemplate restTemplate;

    private String tempName;

    private DetailsDrawer detailsDrawer;
    private DetailsDrawerHeader detailsDrawerHeader;
    private Button btnNew;
    private Grid<TemplateContract> grid;


    private Binder<TemplateContract> binder;
    private DetailsDrawerFooter footer;

    private List<TemplateContract> templateContractList;
    private ListDataProvider<TemplateContract> dataProvider;
    private TemplateContract current;

    private TextField fileName;


    @Override
    protected void onAttach(AttachEvent attachment){
        super.onAttach(attachment);

        getListTemplateContract();
        setViewHeader(createTopBar());
        setViewContent(createContent());
        setViewDetails(createDetailsDrawer());


    }

    private void getListTemplateContract(){
        templateContractList = new ArrayList<>(restTemplate.findAll());
        dataProvider = new ListDataProvider<>(templateContractList);
    }

    private Component createContent(){
        FlexBoxLayout content = new FlexBoxLayout(createGridTemplateContract());
        content.addClassName("grid-view");
        content.setBoxSizing(BoxSizing.BORDER_BOX);
        content.setHeightFull();
        content.setPadding(Horizontal.RESPONSIVE_X, Top.RESPONSIVE_X);
        return content;
    }

    private HorizontalLayout createTopBar(){
        btnNew = new Button("Nueva Plantilla");
        btnNew.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnNew.setIcon(VaadinIcon.PLUS_CIRCLE.create());
        btnNew.addClickShortcut(Key.KEY_N, KeyModifier.ALT);
//        btnNew.setEnabled(GrantOptions.grantedOption("Plantilla Contratos"));
        btnNew.addClickListener(e -> {
            showDetails(new TemplateContract());
        });

        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setWidth("100%");
        topLayout.add(btnNew);
        topLayout.setVerticalComponentAlignment(FlexComponent.Alignment.END,btnNew);
        topLayout.setSpacing(true);
        topLayout.setPadding(true);

        return topLayout;
    }

    private Grid createGridTemplateContract() {

        grid = new Grid<>();
        grid.setWidthFull();
        grid.setHeightFull();

        grid.setDataProvider(dataProvider);

        grid.addSelectionListener( event -> event.getFirstSelectedItem().ifPresent(this::showDetails));

        grid.addColumn(TemplateContract::getFileName).setFlexGrow(1).setHeader("Plantilla")
                .setSortable(true).setResizable(true);
        grid.addColumn(TemplateContract::getDetail).setFlexGrow(1).setHeader("Descripcion");
        grid.addColumn(new ComponentRenderer<>(this::createActive)).setFlexGrow(0).setHeader("Activa");

        return grid;
    }

    private Component createActive(TemplateContract templateContract){
        Icon icon;
        if(templateContract.getActive().equals("SI")){
            icon = UIUtils.createPrimaryIcon(VaadinIcon.CHECK);
        }else{
            icon = UIUtils.createDisabledIcon(VaadinIcon.CLOSE);
        }
        return icon;
    }

    private void showDetails(TemplateContract templateContract){
        current = templateContract;
        tempName ="";
        detailsDrawerHeader.setTitle("Plantilla: "+ templateContract.getFileName());
        detailsDrawer.setContent(createDetails(templateContract));
        detailsDrawer.show();
        binder.readBean(current);
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
                TemplateContract result = null;
                try {
                    TemplateContract temp = restTemplate.getByFileName(fileName.getValue());
                    if(temp.getId()==null){
                        result = (TemplateContract) restTemplate.create(current);
                        if(!tempName.equals("")) {
                            restTemplate.upload(tempName, fileName.getValue());
                        }
                        if (current.getId()==null){
                            templateContractList.add(result);
                            grid.getDataProvider().refreshAll();
                        }else{
                            grid.getDataProvider().refreshItem(current);
                        }
                        detailsDrawer.hide();
                        UIUtils.dialog("Plantilla contrato registrada","success").open();
                    }else{
                        result = (TemplateContract) restTemplate.create(current);
                        if(!tempName.equals("")) {
                            restTemplate.upload(tempName, fileName.getValue());
                        }
                        dataProvider.refreshAll();
                        detailsDrawer.hide();
                        UIUtils.dialog("Plantilla contrato actualizada","success").open();
                    }


                } catch (Exception ex) {
                    UIUtils.dialog(ex.getMessage(),"alert").open();
                }

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

    private FormLayout createDetails(TemplateContract templateContract){
        fileName = new TextField();
        fileName.setWidth("100%");
        fileName.setRequiredIndicatorVisible(true);
        fileName.setRequired(true);

        TextArea detail = new TextArea();
        detail.setWidth("100%");


        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setWidth("100");

        RadioButtonGroup<String> active = new RadioButtonGroup<>();
        active.setItems("SI","NO");


        binder = new BeanValidationBinder<>(TemplateContract.class);
        binder.forField(fileName).asRequired("Nombre de la plantilla es requerido")
                .bind(TemplateContract::getFileName,TemplateContract::setFileName);
        binder.forField(active).asRequired("Indicar el estado de la plantilla")
                .bind(TemplateContract::getActive,TemplateContract::setActive);
        binder.forField(detail).bind(TemplateContract::getDetail,TemplateContract::setDetail);
        binder.addStatusChangeListener(event -> {
            boolean isValid = !event.hasValidationErrors();
            boolean hasChanges = binder.hasChanges();
//            footer.saveState(hasChanges && isValid && GrantOptions.grantedOption("Plantilla Contratos"));
            footer.saveState(hasChanges && isValid);
        });



        upload.addSucceededListener(e ->{
            InputStream inputStream = buffer.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int n = 0;
            try {
                fileName.setValue(e.getFileName());
                while ((n = inputStream.read(buf)) >= 0)
                    baos.write(buf, 0, n);
                byte[] content = baos.toByteArray();
                tempName = tempFile+ UUID.randomUUID().toString()+".docx";
                File f = new File(tempName);
                FileOutputStream fos = new FileOutputStream(f);
                fos.write(content);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        FormLayout formLayout = new FormLayout();
        formLayout.addClassNames(LumoStyles.Padding.Bottom.L,
                LumoStyles.Padding.Horizontal.S, LumoStyles.Padding.Top.S);
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("21em", 2,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP));
//        formLayout.addFormItem(fileName,"Nombre Plantilla");
        formLayout.addFormItem(active,"Plantilla Activa");
        FormLayout.FormItem detailItem = formLayout.addFormItem(detail,"Descripcion plantilla");
        UIUtils.setColSpan(2,detailItem);
        formLayout.addFormItem(upload,"");
        return formLayout;
    }

}
