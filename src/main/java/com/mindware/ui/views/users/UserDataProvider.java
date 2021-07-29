package com.mindware.ui.views.users;

import com.mindware.backend.entity.Users;
import com.vaadin.flow.data.provider.ListDataProvider;

import java.util.Collection;
import java.util.Locale;
import java.util.Objects;

public class UserDataProvider extends ListDataProvider<Users> {
    private String filterText ="";
    public UserDataProvider(Collection<Users> items) {
        super(items);
    }

    public void setFilter(String filterText){
        Objects.requireNonNull(filterText,"Filtro no puede ser omitido");
        if(Objects.equals(this.filterText,filterText.trim())){
            return;
        }
        this.filterText = filterText.trim();

        setFilter(users -> passesFilter(users.getFullName(),filterText)
                || passesFilter(users.getLogin(),filterText)
                || passesFilter(users.getState(),filterText)
                || passesFilter(users.getRolName(),filterText)
        );
    }
    private boolean passesFilter(Object object, String filterText) {
        return object != null && object.toString().toLowerCase(Locale.ENGLISH)
                .contains(filterText.toLowerCase());
    }
}
