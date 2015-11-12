package org.openelis.portal.modules.message.client;

import org.openelis.ui.screen.Screen;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HTML;

public class MessageScreen extends Screen {

    private MessageUI ui = GWT.create(MessageUIImpl.class);

    public MessageScreen() {
        initWidget(ui.asWidget());
        initialize();
    }

    /**
     * Setup state and data change handles for every widget on the screen
     */
    private void initialize() {
        ui.initialize();
    }

    public HTML getMessage() {
        return ui.getMessage();
    }

    public void setMessage(String html) {
        ui.setMessage(html);
    }

}