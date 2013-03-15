package org.openelis.manager;

import org.openelis.constants.OpenELISConstants;

import com.google.gwt.core.client.GWT;

public class MessagesProxy {
    
    public static OpenELISConstants get() {
        return GWT.create(OpenELISConstants.class);
    }

}
