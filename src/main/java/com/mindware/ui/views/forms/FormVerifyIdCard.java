package com.mindware.ui.views.forms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindware.backend.entity.DataIdCard;
import com.mindware.backend.entity.Forms;
import com.mindware.backend.entity.Parameter;
import com.mindware.backend.rest.forms.FormsRestTemplate;
import com.mindware.ui.MainLayout;
import com.mindware.ui.components.FlexBoxLayout;
import com.mindware.ui.layout.size.Horizontal;
import com.mindware.ui.layout.size.Top;
import com.mindware.ui.util.UIUtils;
import com.mindware.ui.util.css.BoxSizing;
import com.mindware.ui.views.SplitViewFrame;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Route(value = "Formulario-Verificacion", layout = MainLayout.class)
@ParentLayout(MainLayout.class)
@PageTitle("Formulario de Verificacion carnets")
public class FormVerifyIdCard extends SplitViewFrame implements RouterLayout {
    @Autowired
    private FormsRestTemplate formsRestTemplate;

    private Button btnNew;

    private List<Forms> formsVerificationIdCardList;

    private List<DataIdCard> dataIdCardList;

    private Grid<Forms> grid;

    private ListDataProvider<Forms> dataProvider;

    private DialogVerificationIdCard dialogVerificationIdCard;

    @Override
    protected void onAttach(AttachEvent attachEvent){
        super.onAttach(attachEvent);

        getListFormsVerification();
        setViewHeader(createTopBar());
        setViewContent(createContent());

    }

    private Component createContent(){
        FlexBoxLayout content = new FlexBoxLayout(createFormVerification());
        content.addClassName("grid-view");
        content.setHeightFull();
        content.setBoxSizing(BoxSizing.BORDER_BOX);
        content.setPadding(Horizontal.RESPONSIVE_X, Top.RESPONSIVE_X);
        return content;
    }

    private void getListFormsVerification(){
        String user = VaadinSession.getCurrent().getAttribute("login").toString();
        List<Forms> list = formsRestTemplate.findByUserTypeFormAndCategoryTypeForm(user,"VERIF. SEGIP","VARIOS");
        formsVerificationIdCardList = new ArrayList<>(list)  ;
        dataProvider = new ListDataProvider<>(formsVerificationIdCardList);
    }

    private HorizontalLayout createTopBar(){
        btnNew = new Button("Nuevo Formulario");
        btnNew.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnNew.setIcon(VaadinIcon.PLUS_CIRCLE.create());
        btnNew.addClickShortcut(Key.KEY_N, KeyModifier.ALT);
//        btnNew.setEnabled(GrantOptions.grantedOption("Parametros"));
        btnNew.addClickListener(click -> {
            Forms forms = new Forms();
            Button btnSave = new Button("Guardar");
            btnSave.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

            Button btnClose = new Button("Cancelar");
            btnClose.addThemeVariants(ButtonVariant.LUMO_PRIMARY,ButtonVariant.LUMO_CONTRAST);
            btnClose.addClickListener(event -> dialogVerificationIdCard.close());

            btnSave.addClickListener(event ->{
                forms.setNameTypeForm("VERIF. SEGIP");
                forms.setCategoryTypeForm("VARIOS");
                forms.setOriginModule("AUTOFORM");
                forms.setIdCardForVerification(dialogVerificationIdCard.getDataIdCardListString());
                forms.setIdUser(VaadinSession.getCurrent().getAttribute("login").toString());
                if(forms.getCreationDate()==null){
                    Date currentDate = (Date) VaadinSession.getCurrent().getAttribute("current-date");

                    forms.setCreationDate(currentDate);
                    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
                    Date dateHour = new Date();
                    String hour = formatter.format(dateHour);
                    forms.setCreationTime(hour);
                }

                Forms newForms = formsRestTemplate.create(forms);
                formsVerificationIdCardList.add(newForms);
                dataProvider.refreshAll();

                UIUtils.dialog("Lista documentos para verificacion creados","success").open();
                dialogVerificationIdCard.close();
            });

            dialogVerificationIdCard = new DialogVerificationIdCard(forms, formsRestTemplate);
            dialogVerificationIdCard.footer.add(btnSave,btnClose);
            dialogVerificationIdCard.open();

        });

        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setWidth("100%");
        topLayout.add(btnNew);
        topLayout.setVerticalComponentAlignment(FlexComponent.Alignment.END,btnNew);
        topLayout.setSpacing(true);
        topLayout.setPadding(true);

        return topLayout;
    }

    private Grid createFormVerification() {
        grid = new Grid();
        grid.setId("verification");
        grid.setMultiSort(true);
        grid.setHeightFull();
        grid.setWidthFull();

        grid.setDataProvider(dataProvider);
        grid.addColumn(new LocalDateRenderer<>( Forms::getCreationDateConverter,  DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .setFlexGrow(0)
                .setHeader("Fecha creacion")
                .setSortable(true)
                .setAutoWidth(true)
                .setResizable(true);
        grid.addColumn(Forms::getCreationTime)
                .setFlexGrow(0)
                .setHeader("Hora creacion")
                .setSortable(true)
                .setAutoWidth(true)
                .setResizable(true);
        grid.addColumn(new ComponentRenderer<>(this::createTaskGrid))
                .setAutoWidth(true)
                .setFlexGrow(0);

        return grid;
    }

    private Component createTaskGrid(Forms forms){
        Button btnEdit = new Button("Editar");
        btnEdit.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);
        btnEdit.setIcon(VaadinIcon.FILE_ADD.create());

        Button btnPrint = new Button();
        btnPrint.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_CONTRAST);
        btnPrint.setIcon(VaadinIcon.PRINT.create());

        HorizontalLayout layout = new HorizontalLayout();
        layout.add(btnEdit,btnPrint);

        btnEdit.addClickListener(click -> {
            Button btnSave = new Button("Guardar");
            btnSave.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

            Button btnClose = new Button("Cancelar");
            btnClose.addThemeVariants(ButtonVariant.LUMO_PRIMARY,ButtonVariant.LUMO_CONTRAST);
            btnClose.addClickListener(event -> dialogVerificationIdCard.close());

            dialogVerificationIdCard = new DialogVerificationIdCard(forms, formsRestTemplate);
            dialogVerificationIdCard.footer.add(btnSave, btnClose);
            btnSave.addClickListener(event ->{
               forms.setIdCardForVerification(dialogVerificationIdCard.getDataIdCardListString());
               if(forms.getIdUser()==null || forms.getIdUser().equals("")){
                   forms.setIdUser(VaadinSession.getCurrent().getAttribute("login").toString());
               }
               formsRestTemplate.create(forms);
               dialogVerificationIdCard.close();
               formsVerificationIdCardList.removeIf(f -> f.getId().equals(forms.getId()));
               formsVerificationIdCardList.add(forms);
               dataProvider.refreshAll();
               UIUtils.dialog("Datos actualizados","succes").open();
            });
            dialogVerificationIdCard.open();
        });

        btnPrint.addClickListener(click -> {
            if(forms.getIdUser()!=null) {
                FormReportView report = new FormReportView(0, "",
                        "VERIF. SEGIP", "VARIOS", formsRestTemplate, forms.getId(), forms.getIdUser(), null,"NO","NO");
                report.open();
            }else{
                UIUtils.dialog("Edite el formulario y guarde para que se asigne el formulario a su usuario","alert").open();
            }
        });

        return layout;

    }
}
