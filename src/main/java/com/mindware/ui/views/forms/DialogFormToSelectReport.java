package com.mindware.ui.views.forms;

import com.mindware.backend.entity.Forms;
import com.mindware.backend.entity.dto.FormToSelectReportDto;
import com.mindware.backend.entity.netbank.dto.DataFormDto;
import com.mindware.backend.entity.netbank.dto.GbageDto;
import com.mindware.backend.rest.forms.FormsRestTemplate;
import com.mindware.backend.rest.netbank.GbageDtoRestTemplate;
import com.mindware.ui.util.UIUtils;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.Lumo;
import org.vaadin.gatanaso.MultiselectComboBox;

import java.util.List;
import java.util.stream.Collectors;

@CssImport("./styles/my-dialog.css")
public class DialogFormToSelectReport extends Dialog {

    public String DOCK = "dock";
    public String FULLSCREEN = "fullscreen";

    private boolean isDocked = false;
    private boolean isFullScreen = false;

    private Header header;
    private Button min;
    private Button max;
    private Button btnClose;
    private Button discardDraft;

    private VerticalLayout content;
    private Footer footer;

    private FormsRestTemplate formsRestTemplateGlobal;

    public DialogFormToSelectReport(List<FormToSelectReportDto> formToSelectReportDtoList,
                                    FormsRestTemplate formsRestTemplate){
        setDraggable(true);
        setModal(false);
        setResizable(true);

        formsRestTemplateGlobal = formsRestTemplate;

        // Dialog theming
        getElement().getThemeList().add("my-dialog");
        setWidth("300px");

        // Accessibility
        getElement().setAttribute("aria-labelledby", "dialog-title");

        // Header
        String textTitle="SELECCION DE REPORTES";
        H2 title = new H2(textTitle );
        title.addClassName("dialog-title");

        min = new Button(VaadinIcon.DOWNLOAD_ALT.create());
        min.addClickListener(event -> minimise());

        max = new Button(VaadinIcon.EXPAND_SQUARE.create());
        max.addClickListener(event -> maximise());


        Button close = new Button(VaadinIcon.CLOSE_SMALL.create());
        close.addClickListener(event -> close());

        header = new Header(title, min, max, close);
        header.getElement().getThemeList().add(Lumo.DARK);
        add(header);

        btnClose = new Button("Cerrar");
        discardDraft = new Button(VaadinIcon.TRASH.create());

        //Content

        content = new VerticalLayout(formListReportToGenerate(formToSelectReportDtoList));
        content.addClassName("dialog-content");
        content.setAlignItems(FlexComponent.Alignment.STRETCH);
        add(content);

        //Footer

        btnClose.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_CONTRAST);

//        discardDraft.addThemeVariants(ButtonVariant.LUMO_ERROR,ButtonVariant.LUMO_TERTIARY);
        footer = new Footer(btnClose);
        add(footer);

        // Button theming
        for (Button button : new Button[] { min, max, close }) {
            button.addThemeVariants(ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_TERTIARY);
        }
        maximise();

        btnClose.addClickListener(event -> {
            close();
        });
    }

    private HorizontalLayout formListReportToGenerate(List<FormToSelectReportDto> formToSelectReportDtoList){
        MultiselectComboBox<FormToSelectReportDto> multiselectComboBox = new MultiselectComboBox();
        multiselectComboBox.setWidth("80%");
//        multiselectComboBox.setLabel("Selecione formularios a imprimir");
        multiselectComboBox.setPlaceholder("Seleccione formularios a imprimir...");
        multiselectComboBox.setItems(formToSelectReportDtoList);

        multiselectComboBox.setRenderer(new ComponentRenderer<VerticalLayout,FormToSelectReportDto>(VerticalLayout::new,(container, formToSelectReportDto) -> {
            HorizontalLayout typeForm = new HorizontalLayout(new Icon(VaadinIcon.NEWSPAPER),
                    new Label(formToSelectReportDto.getNameTypeForm()+"*"+ formToSelectReportDto.getCategoryTypeForm()));
            HorizontalLayout client = new HorizontalLayout(new Icon(VaadinIcon.CLIPBOARD_USER),
                    new Label("Código Cliente: " + formToSelectReportDto.getIdClient()));

            HorizontalLayout account = new HorizontalLayout(new Icon(VaadinIcon.BOOK_DOLLAR),
                    new Label("Cuenta: " +
                            (formToSelectReportDto.getNameTypeForm()
                                    .equals("FORMULARIO APERTURA")?formToSelectReportDto.getIdAccount():formToSelectReportDto.getAccount())));

            HorizontalLayout idaccount = new HorizontalLayout(new Icon(VaadinIcon.BOOK_DOLLAR),
                    new Label("ID: " + formToSelectReportDto.getIdAccount()));
            idaccount.setVisible(false);
            container.add(typeForm,client,account,idaccount);
        }));

        multiselectComboBox.setItemLabelGenerator(FormToSelectReportDto::getTypFormAccount );

        Button btnPrint = new Button("Imprimir");
        btnPrint.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnPrint.addClickListener(event -> {
            String list = multiselectComboBox.getSelectedItems()
                    .stream()
                    .map(FormToSelectReportDto::getTypFormAccount)
                    .collect(Collectors.joining("&","",""));
            if(!list.isEmpty()) {

                FormReportView report = new FormReportView(formToSelectReportDtoList.get(0).getIdClient(),
                        VaadinSession.getCurrent().getAttribute("name-office").toString(), "REPORTS", "SELECTED", formsRestTemplateGlobal, list,
                        VaadinSession.getCurrent().getAttribute("login").toString(), VaadinSession.getCurrent().getAttribute("login").toString(), null, "", "");
                report.open();
            }else {
                UIUtils.showNotificationType("Seleccione los reportes a imprimir","alert");
            }

        });

        HorizontalLayout layout = new HorizontalLayout();

        layout.add(multiselectComboBox,btnPrint);

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
        footer.setVisible(!isDocked);
    }

    private void initialSize() {
        min.setIcon(VaadinIcon.DOWNLOAD_ALT.create());
        getElement().getThemeList().remove(DOCK);
        max.setIcon(VaadinIcon.EXPAND_SQUARE.create());
        getElement().getThemeList().remove(FULLSCREEN);
        setHeight("400px");
        setWidth("500px");
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
//            setSizeFull();
            setWidth("50%");
            setHeight("50%");
            content.setVisible(true);
            footer.setVisible(true);
        }
        isFullScreen = !isFullScreen;
        isDocked = false;
    }
}
