package org.openelis.portal.modules.message.client;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

public interface MessageUI extends IsWidget {

    public void initialize();

    public HTML getMessage();

    public void setMessage(String message);

}