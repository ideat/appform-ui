package com.mindware.ui.views.forms;

import com.mindware.backend.rest.forms.FormsRestTemplate;
import com.mindware.ui.components.FlexBoxLayout;
import com.mindware.ui.components.detailsdrawer.DetailsDrawer;
import com.mindware.ui.layout.size.Horizontal;
import com.mindware.ui.layout.size.Top;
import com.mindware.ui.util.EmbeddedPdfDocument;
import com.mindware.ui.util.css.BoxSizing;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.Lumo;

import java.io.ByteArrayInputStream;

@CssImport("./styles/my-dialog.css")
public class FormReportView extends Dialog  {
    public String DOCK = "dock";
    public String FULLSCREEN = "fullscreen";

    private boolean isDocked = false;
    private boolean isFullScreen = false;

    private Header header;
    private Button min;
    private Button max;

    private byte[] file;
    private FlexBoxLayout contentReport;


    public FormReportView(Integer codeClient, String idAccount, String typeForm, String categoryTypeForm, FormsRestTemplate restTemplate){
        setDraggable(true);
        setModal(false);
        setResizable(true);

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


        Button close = new Button(VaadinIcon.CLOSE_SMALL.create());
        close.addClickListener(event -> close());

        header = new Header(title, min, max, close);
        header.getElement().getThemeList().add(Lumo.DARK);
        add(header);


        if(categoryTypeForm.equals("CAJA-AHORRO") || categoryTypeForm.equals("DPF")){

            file = restTemplate.report(codeClient,idAccount,typeForm,categoryTypeForm);
        }

        contentReport = (FlexBoxLayout) createContent(createReportView());

        add(contentReport);
        maximise();
    }

    private Component createContent(DetailsDrawer component){
        FlexBoxLayout content = new FlexBoxLayout(component);
        content.setFlexDirection(FlexLayout.FlexDirection.ROW);
        content.setBoxSizing(BoxSizing.BORDER_BOX);
        content.setHeightFull();
        content.setWidthFull();
        content.setPadding(Horizontal.RESPONSIVE_X, Top.RESPONSIVE_X);

        return content;
    }

    private DetailsDrawer createReportView(){
        Div layout = new Div();
        layout.setSizeFull();
        ByteArrayInputStream bis = new ByteArrayInputStream(file);
        StreamResource s = new StreamResource("reporte.pdf", () -> bis);
        layout.add(new EmbeddedPdfDocument(s));

        DetailsDrawer verticalLayout = new DetailsDrawer(DetailsDrawer.Position.BOTTOM);
        verticalLayout.add(layout);
        verticalLayout.setSizeFull();

        return verticalLayout;
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
        contentReport.setVisible(!isDocked);
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
            contentReport.setVisible(true);
//            footer.setVisible(true);
        }
        isFullScreen = !isFullScreen;
        isDocked = false;
    }

}
