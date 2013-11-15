package org.openelis.web.modules.followup.client;

import org.openelis.ui.screen.Screen;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;

public class ConditionTab extends Screen {
    
    @UiTemplate("Condition.ui.xml")
    interface ConditionUiBinder extends UiBinder<Widget,ConditionTab>{};
    public static final ConditionUiBinder uiBinder = GWT.create(ConditionUiBinder.class);
    
    public ConditionTab() {
        initWidget(uiBinder.createAndBindUi(this));
    }
    

}
