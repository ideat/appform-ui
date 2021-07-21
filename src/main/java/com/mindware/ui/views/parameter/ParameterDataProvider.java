package com.mindware.ui.views.parameter;

import com.mindware.backend.entity.Parameter;
import com.vaadin.flow.data.provider.ListDataProvider;

import java.util.Collection;
import java.util.Locale;
import java.util.Objects;

public class ParameterDataProvider extends ListDataProvider<Parameter> {

    private String valueFilter = "";
    private String nameFilter = "";
    private String categoryFilter = "";

    public ParameterDataProvider(Collection<Parameter> items) {
        super(items);
    }

    public void setFilter(String valueFilter, String nameFilter, String categoryFilter){
        Objects.requireNonNull(valueFilter, "Filtro valor no puede ser nulo");
        Objects.requireNonNull(categoryFilter, "Filtro categoria no puede ser nulo");
        Objects.requireNonNull(nameFilter, "Filtro descripcion no puede ser nulo");
        if (Objects.equals(this.valueFilter,valueFilter.trim())
                && Objects.equals(this.categoryFilter,categoryFilter)
                && Objects.equals(this.nameFilter,nameFilter)){
            return;
        }
        this.valueFilter = valueFilter.trim();
        this.categoryFilter = categoryFilter.trim();
        this.nameFilter = nameFilter.trim();


        setFilter(parameter ->
                passesFilter(parameter.getValue(), valueFilter)
                        || passesFilter(parameter.getCategory(), categoryFilter)
                        || passesFilter(parameter.getName(), nameFilter)
        );
    }

    private boolean passesFilter(Object object, String filterText) {
        return object != null && object.toString().toLowerCase(Locale.ENGLISH)
                .contains(filterText.toLowerCase());
    }
}
