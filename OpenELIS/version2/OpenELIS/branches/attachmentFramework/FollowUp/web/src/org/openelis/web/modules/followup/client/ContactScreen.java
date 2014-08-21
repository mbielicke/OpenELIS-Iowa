package org.openelis.web.modules.followup.client;

import org.openelis.ui.screen.Screen;
import org.openelis.ui.widget.AutoComplete;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.TextArea;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.calendar.Calendar;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;

public class ContactScreen extends Screen {
    
    @UiTemplate("Contact.ui.xml")
    interface ContactUiBinder extends UiBinder<Widget,ContactScreen>{};
    public static final ContactUiBinder uiBinder = GWT.create(ContactUiBinder.class);
    
    @UiField
    protected TextBox<String> by,inReference,initiatedBy,contactDetails;
    
    @UiField
    protected AutoComplete    organization;
    
    @UiField
    protected Dropdown<Integer> personContacted, contactMethod;
    
    @UiField
    protected Calendar          contactTime;
    
    @UiField
    protected TextArea          comments; 
    
    public ContactScreen() {
        initWidget(uiBinder.createAndBindUi(this));
    }
    
}
