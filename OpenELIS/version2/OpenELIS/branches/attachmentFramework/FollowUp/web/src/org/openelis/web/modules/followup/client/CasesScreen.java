package org.openelis.web.modules.followup.client;

import org.openelis.constants.Messages;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.common.PermissionException;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.WindowInt;
import org.openelis.ui.widget.table.Table;
import org.openelis.ui.widget.tree.Tree;
import org.openelis.web.cache.UserCache;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;

public class CasesScreen extends Screen {
    
    @UiTemplate("Cases.ui.xml")
    interface CasesUiBinder extends UiBinder<Widget, CasesScreen>{};
    public static final CasesUiBinder uiBinder = GWT.create(CasesUiBinder.class);
    
    @UiField
    Dropdown<Integer> tag;
    
    @UiField
    TextBox<String> searchCases;
    
    @UiField
    Button          takeCase;
    
    @UiField
    Tree            navigation;
    
    @UiField 
    Table           cases;
    
    @UiField(provided=true)
    PatientTab      patientTab;
    
    private ModulePermission            userPermission;
    
    public CasesScreen(WindowInt window) throws PermissionException {
        this.window = window;
        
        /*
        userPermission = UserCache.getPermission().getModule("w_followup");
        if (userPermission == null)
            throw new PermissionException(Messages.get().screenPermException("Follow Up Cases Screen"));
        */
        
        patientTab = new PatientTab(this);
        
        initWidget(uiBinder.createAndBindUi(this));
        
        initialize();
    }
    
    private void initialize() {
        
    }

}
