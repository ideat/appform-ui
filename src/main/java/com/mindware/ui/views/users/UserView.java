package com.mindware.ui.views.users;

import com.mindware.backend.entity.Users;
import com.mindware.backend.rest.email.MailRestTemplate;
import com.mindware.backend.rest.netbank.AdusrOfiRestTemplate;
import com.mindware.backend.rest.user.UserRestTemplate;
import com.mindware.backend.util.PrepareMail;
import com.mindware.ui.MainLayout;
import com.mindware.ui.components.FlexBoxLayout;
import com.mindware.ui.components.ListItem;
import com.mindware.ui.layout.size.Horizontal;
import com.mindware.ui.layout.size.Top;
import com.mindware.ui.util.UIUtils;
import com.mindware.ui.util.Util;
import com.mindware.ui.util.css.BoxSizing;
import com.mindware.ui.views.ViewFrame;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Route(value = "user-view", layout = MainLayout.class)
@ParentLayout(MainLayout.class)
@PageTitle("Usuarios")
public class UserView extends ViewFrame implements RouterLayout {

    @Autowired
    private UserRestTemplate restTemplate;

    @Autowired
    private PrepareMail prepareMail;

    @Autowired
    private AdusrOfiRestTemplate adusrOfiRestTemplate;

    private UserDataProvider dataProvider;
    private List<Users> usersList;
    private TextField filterText;
    private Button btnNew;



    @Override
    protected void onAttach(AttachEvent attachment) {
        super.onAttach(attachment);
        getUserList();
        setViewContent(createContent());
        setViewHeader(createTopBar());
    }

    private void getUserList(){
        usersList = new ArrayList<>(restTemplate.findAll());
        usersList.sort(Comparator.comparing(Users::getLogin));
        dataProvider = new UserDataProvider(usersList);
    }

    private Component createContent(){
        FlexBoxLayout content = new FlexBoxLayout(createLayout());
        content.addClassName("grid-view");
        content.setBoxSizing(BoxSizing.BORDER_BOX);
        content.setHeightFull();
        content.setPadding(Horizontal.RESPONSIVE_X, Top.RESPONSIVE_X);
        return content;
    }

    private HorizontalLayout createTopBar(){
        filterText = new TextField();
        filterText.setPlaceholder("Filtro por Login, Nombre, Estado, Rol");
        filterText.addFocusShortcut(Key.KEY_F, KeyModifier.CONTROL);
        filterText.addValueChangeListener(event -> dataProvider.setFilter(event.getValue()));

        btnNew = new Button("Nuevo");
        btnNew.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnNew.setIcon(VaadinIcon.PLUS_CIRCLE.create());
//        btnNew.setEnabled(GrantOptions.grantedOption("Usuarios"));
        btnNew.addClickShortcut(Key.KEY_N, KeyModifier.ALT);
        btnNew.addClickListener(e ->{
            DialogUserCreate dialogUserCreate = new DialogUserCreate(restTemplate,new Users(), prepareMail, adusrOfiRestTemplate);
            Button btnSave = new Button("Guardar");
            btnSave.addThemeVariants(ButtonVariant.LUMO_PRIMARY,ButtonVariant.LUMO_SMALL);
            btnSave.addClickListener(click -> {
                Users newUser = dialogUserCreate.saveUser();
               if(newUser!=null){
                   usersList.add(newUser);
                   usersList.sort(Comparator.comparing(Users::getLogin));
//                   getUserList();
                   dataProvider.refreshAll();
               }

            });
            dialogUserCreate.footer.add(btnSave);
            dialogUserCreate.open();
        });

        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setWidth("100%");
        topLayout.add(filterText);
        topLayout.setVerticalComponentAlignment(FlexComponent.Alignment.START,filterText);
        topLayout.expand(filterText);
        topLayout.add(btnNew);
        topLayout.setVerticalComponentAlignment(FlexComponent.Alignment.START,btnNew);
        topLayout.setSpacing(true);

        return topLayout;
    }

    private VerticalLayout createLayout(){
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();


        Grid<Users> grid = new Grid<>();

        grid.setDataProvider(dataProvider);
        grid.setSizeFull();
        grid.addSelectionListener(e ->{
//            UI.getCurrent().navigate(UsersRegister.class,e.getFirstSelectedItem().get().getId().toString());
        });

        grid.addColumn(Users::getLogin).setFlexGrow(1).setFrozen(true)
                .setHeader("Login").setSortable(true)
                .setAutoWidth(true).setFlexGrow(0).setResizable(true);
        grid.addColumn(new ComponentRenderer<>(this::createUserInfo)).setFlexGrow(1)
                .setHeader("Nombre").setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(new ComponentRenderer<>(this::createActive))
                .setFlexGrow(1).setHeader("Estado")
                .setAutoWidth(true).setFlexGrow(0)
                .setTextAlign(ColumnTextAlign.END);
        grid.addColumn(Users::getRolName)
                .setAutoWidth(true).setFlexGrow(0).setHeader("Rol").setSortable(true);
        grid.addColumn(new ComponentRenderer<>(this::createDate))
                .setHeader("Fecha cambio password").setAutoWidth(true).setFlexGrow(0)
                .setTextAlign(ColumnTextAlign.CENTER);
        grid.addColumn(new ComponentRenderer<>(this::createEditButton))
                .setAutoWidth(true).setFlexGrow(0);

        layout.add(grid);

        return layout;
    }

    private Component createEditButton(Users user){
        Button btnEdit = new Button("Editar");
        btnEdit.addThemeVariants(ButtonVariant.LUMO_PRIMARY,ButtonVariant.LUMO_SUCCESS,ButtonVariant.LUMO_SMALL);
        btnEdit.addClickListener(event ->{
            DialogUserCreate dialogUserCreate = new DialogUserCreate(restTemplate, user, prepareMail,adusrOfiRestTemplate);
            Button btnSave = new Button("Actualizar");
            dialogUserCreate.login.setReadOnly(true);
            btnSave.addThemeVariants(ButtonVariant.LUMO_PRIMARY,ButtonVariant.LUMO_SMALL);
            btnSave.addClickListener(click -> {
                Users userupdate = dialogUserCreate.saveUser();
                if(userupdate!=null) {
                    usersList.removeIf(u->u.getId().equals(user.getId()));
                    usersList.add(userupdate);

                    restTemplate.update(userupdate);
//                    getUserList();
                    usersList.sort(Comparator.comparing(Users::getLogin));
                    dataProvider.refreshAll();
                    UI.getCurrent().getPage().reload();
                }

            });
            dialogUserCreate.footer.add(btnSave);
            dialogUserCreate.open();
        });

        return btnEdit;

    }

    private Component createUserInfo(Users users){
        ListItem item = new ListItem(
                UIUtils.createInitials(users.getInitials()), users.getFullName(),
                users.getEmail());
        item.setHorizontalPadding(false);
        return item;
    }

    private Component createActive(Users users) {
        Icon icon;
        if (users.getState().equals("ACTIVO")) {
            icon = UIUtils.createPrimaryIcon(VaadinIcon.CHECK);
        } else {
            icon = UIUtils.createDisabledIcon(VaadinIcon.CLOSE);
        }
        return icon;
    }

    private Component createDate(Users users) {

        return new Span(Util.formatDate(users.getDateUpdatePassword(),"dd/MM/yyyy"));
    }

}
