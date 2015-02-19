package org.openelis.portal.messages;

import org.openelis.domain.Constants;

import com.google.gwt.core.shared.GWT;
import net.lightoze.gwt.i18n.client.LocaleFactory;

/**
 * This class provides access to the OpenELIS constant properties for messages, labels, formats,
 * etc. The get method in this class can be called from EJB and GWT environment.
 */
public class Messages {

    private static PortalMessages msgs;
    
    public static PortalMessages get() {
        if (msgs == null)
            initialize();
        return msgs;
    }

    private Messages() {
    }

    private static void initialize() {
        if (GWT.isClient()) {
            msgs = GWT.create(PortalMessages.class);
        } else {
            /*
             * the com.teklabs.gwt.i18n.server.LocaleProxy.initialize() is called in
             * Application startup bean.
             */
            msgs = LocaleFactory.get(PortalMessages.class, Constants.systemProperty().LOCALE);            
        }
    }
}
