package org.openelis.portal.client.resources;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ClientBundle;

public interface Resources extends ClientBundle {
    
    public static final Resources INSTANCE = GWT.create(Resources.class);

    @Source("css/style.css")
    Style style();
    
    

}
