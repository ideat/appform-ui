package com.mindware.ui.views.forms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindware.backend.entity.DataIdCard;
import com.mindware.backend.entity.Forms;
import com.mindware.backend.rest.forms.FormsRestTemplate;
import com.mindware.ui.util.UIUtils;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
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
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.theme.lumo.Lumo;

import java.util.ArrayList;
import java.util.List;

@CssImport("./styles/my-dialog.css")
public class DialogVerificationIdCard extends Dialog {
    public String DOCK = "dock";
    public String FULLSCREEN = "fullscreen";

    private boolean isDocked = false;
    private boolean isFullScreen = false;

    private Header header;
    private Button min;
    private Button max;
    private Button btnSave;


    private VerticalLayout content;
    public Footer footer;

    private List<DataIdCard> dataIdCardList;

    private Grid<DataIdCard> gridDataIdCard;

    private Forms formsVerifyIdCard;

    private TextField fullName;

    private TextField idCard;

    private ComboBox<String> extension;

    public DialogVerificationIdCard(Forms forms, FormsRestTemplate formsRestTemplate){

        setDraggable(true);
        setModal(false);
        setResizable(true);

        formsVerifyIdCard = forms;
        fillListVerifiyIdCard();

        // Dialog theming
        getElement().getThemeList().add("my-dialog");
        setWidth("800px");

        // Accessibility
        getElement().setAttribute("aria-labelledby", "dialog-title");

        // Header
        String textTitle="REGISTRO DOCUMENTOS PARA VERIFICACION";
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

        btnSave = new Button("Guardar");

        //Content

        content = new VerticalLayout(createFormLayout(), createLayout());
        content.addClassName("dialog-content");
        content.setAlignItems(FlexComponent.Alignment.STRETCH);
        add(content);

        //Footer


        footer = new Footer();
        add(footer);

        // Button theming
        for (Button button : new Button[] { min, max, close }) {
            button.addThemeVariants(ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_TERTIARY);
        }
//        maximise();

    }

    public String getDataIdCardListString(){
        ObjectMapper mapper = new ObjectMapper();
        String idVerif="";
        try {
            idVerif = mapper.writeValueAsString(dataIdCardList);
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
        }
        return idVerif;
    }

    private VerticalLayout createFormLayout(){
        VerticalLayout layout = new VerticalLayout();

        FormLayout formLayout = new FormLayout();

        fullName = new TextField();
        fullName.setWidthFull();

        idCard = new TextField();
        idCard.setWidthFull();

        extension = new ComboBox();
        extension.setAllowCustomValue(true);
        String[] ex = {"CB","LP","PA","OR","PO","CH","TJ","OR","BE","SC",""};
        extension.setItems(ex);
        extension.setWidthFull();

        formLayout.addFormItem(idCard,"Carnet o NIT");
        formLayout.addFormItem(extension,"Emitido en");
        formLayout.addFormItem(fullName,"Nombre completo y/o Razon Social");


        HorizontalLayout horizontalLayout = new HorizontalLayout();
        Button btnNew = new Button("Nuevo");
        btnNew.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button btnAdd = new Button("Añadir");
        btnAdd.addThemeVariants(ButtonVariant.LUMO_PRIMARY,ButtonVariant.LUMO_SUCCESS);

        horizontalLayout.add(btnNew, btnAdd);
        horizontalLayout.setSpacing(true);

        btnNew.addClickListener(click -> {
           fullName.clear();
           extension.setValue("");
           idCard.clear();
        });

        btnAdd.addClickListener(click -> {
           if(fullName.isEmpty()){
               fullName.focus();
               UIUtils.dialog("Ingrese el nombre","alert");
               return;
           }
           if(idCard.isEmpty()){
               idCard.focus();
               UIUtils.dialog("Ingrese el numero de documento","alert");
               return;
           }
           DataIdCard dataIdCard = new DataIdCard();
           dataIdCard.setFullName(fullName.getValue());
           dataIdCard.setExtension(extension.getValue()!=null?extension.getValue():"");
           dataIdCard.setNumberId(idCard.getValue());

           dataIdCardList.removeIf(p -> p.getNumberId().equals(idCard.getValue())
                   && p.getExtension().equals(extension.getValue()));

           dataIdCardList.add(dataIdCard);
           gridDataIdCard.setItems(dataIdCardList);

           fullName.clear();
           extension.setValue("");
           idCard.clear();
        });

        layout.add(formLayout,horizontalLayout);

        return layout;

    }

    private VerticalLayout createLayout() {
        VerticalLayout layout = new VerticalLayout();

        gridDataIdCard = new Grid<>();
        gridDataIdCard.setItems(dataIdCardList);
        gridDataIdCard.addColumn(DataIdCard::getNumberId)
                .setHeader("Numero identificacion")
                .setFlexGrow(0)
                .setResizable(true)
                .setAutoWidth(true);
        gridDataIdCard.addColumn(DataIdCard::getExtension)
                .setHeader("Extension")
                .setFlexGrow(0)
                .setResizable(true)
                .setAutoWidth(true);
        gridDataIdCard.addColumn(DataIdCard::getFullName)
                .setHeader("Nombre completo")
                .setFlexGrow(0)
                .setResizable(true)
                .setAutoWidth(true);
        gridDataIdCard.addColumn(new ComponentRenderer<>(this::createTaskGrid))
                .setFlexGrow(0)
                .setResizable(true)
                .setAutoWidth(true);

        layout.add(gridDataIdCard);


        return layout;
    }

    private Component createTaskGrid(DataIdCard dataIdCard){
        Button btnEdit = new Button("Editar");
        btnEdit.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);
        btnEdit.setIcon(VaadinIcon.FILE_ADD.create());

        Button btnDelete = new Button("Borrar");
        btnDelete.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
        btnDelete.setIcon(VaadinIcon.TRASH.create());

        HorizontalLayout layout = new HorizontalLayout();
        layout.add(btnEdit,btnDelete);

        btnDelete.addClickListener(click -> {
           dataIdCardList.removeIf(d -> d.getNumberId().equals(dataIdCard.getNumberId())
                    && d.getExtension().equals(dataIdCard.getExtension()));
           gridDataIdCard.setItems(dataIdCardList);
            UIUtils.dialog("Registro fue removido","info");
        });

        btnEdit.addClickListener(click -> {
            fullName.setValue(dataIdCard.getFullName());
            idCard.setValue(dataIdCard.getNumberId());
            extension.setValue(dataIdCard.getExtension());
        });

        return layout;
    }

    private void fillListVerifiyIdCard(){
        ObjectMapper mapper = new ObjectMapper();
        String listString = formsVerifyIdCard.getIdCardForVerification();
        if(listString==null || listString.isEmpty() || listString.equals("[]")){
            dataIdCardList = new ArrayList<>();
        }else{
            try {
                dataIdCardList = mapper.readValue(listString, new TypeReference<List<DataIdCard>>() {});
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
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
        footer.setVisible(!isDocked);
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
            footer.setVisible(true);
        }
        isFullScreen = !isFullScreen;
        isDocked = false;
    }
}
