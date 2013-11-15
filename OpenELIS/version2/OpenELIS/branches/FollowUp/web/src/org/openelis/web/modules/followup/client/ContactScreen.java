package org.openelis.web.modules.followup.client;

import org.openelis.ui.screen.Screen;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;

public class ContactScreen extends Screen {
    
    @UiTemplate("Contact.ui.xml")
    interface ContactUiBinder extends UiBinder<Widget,ContactScreen>{};
    public static final ContactUiBinder uiBinder = GWT.create(ContactUiBinder.class);
    
    public ContactScreen() {
        initWidget(uiBinder.createAndBindUi(this));
    }
    
}
