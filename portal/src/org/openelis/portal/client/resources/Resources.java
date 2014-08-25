package org.openelis.portal.client.resources;

import org.openelis.ui.resources.UIResources;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ClientBundle;

public interface Resources extends UIResources {
    
    public static final Resources INSTANCE = GWT.create(Resources.class);

    @Source("css/style.css")
    Style style();
    
    @Source("css/icons.css")
    IconCSS icons();
    
    

}
