package org.openelis.web.modules.followup.client;

import org.openelis.ui.screen.Screen;
import org.openelis.ui.widget.columnar.Columnar;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;

public class PatientTab extends Screen {
    
    @UiTemplate("Patient.ui.xml")
    interface PatientUiBinder extends UiBinder<Widget,PatientTab>{};
    public static final PatientUiBinder uiBinder = GWT.create(PatientUiBinder.class);
    
    @UiField
    Columnar            results;
    
    public PatientTab(Screen parentScreen) {
        initWidget(uiBinder.createAndBindUi(this));
        
        initialize();
    }
    
    private void initialize() {

    }

}
