package com.mindware.ui.views.forms;

import com.mindware.backend.entity.netbank.dto.GbageDto;
import com.vaadin.flow.data.provider.ListDataProvider;

import java.util.Collection;

public class GbageDtoDataProvider extends ListDataProvider<GbageDto> {

    public GbageDtoDataProvider(Collection<GbageDto> items) {
        super(items);
    }
}
