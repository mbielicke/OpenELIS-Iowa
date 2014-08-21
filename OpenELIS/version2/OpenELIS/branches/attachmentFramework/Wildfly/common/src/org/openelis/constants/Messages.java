package org.openelis.constants;

import net.lightoze.gwt.i18n.client.LocaleFactory;

import org.openelis.domain.Constants;

import com.google.gwt.core.shared.GWT;

/**
 * This class provides access to the OpenELIS constant properties for messages, labels, formats,
 * etc. The get method in this class can be called from EJB and GWT environment.
 */
public class Messages {

    private static OpenELISConstants consts;
    
    public static OpenELISConstants get() {
        if (consts == null)
            initialize();
        return consts;
    }

    private Messages() {
    }

    private static void initialize() {
        if (GWT.isClient()) {
            consts = GWT.create(OpenELISConstants.class);
        } else {
            /*
             * the com.teklabs.gwt.i18n.server.LocaleProxy.initialize() is called in
             * Application startup bean.
             */
            consts = LocaleFactory.get(OpenELISConstants.class, Constants.systemProperty().LOCALE);            
        }
    }
}
