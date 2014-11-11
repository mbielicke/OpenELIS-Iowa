package org.openelis.portal.modules.emailNotification.client;

import static org.openelis.ui.screen.State.QUERY;

import org.openelis.portal.cache.UserCache;
import org.openelis.portal.messages.Messages;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.screen.Screen;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;

public class EmailNotificationScreen extends Screen {

    private EmailNotificationUI ui = GWT.create(EmailNotificationUIImpl.class);

    private ModulePermission    userPermission;

    public EmailNotificationScreen() {
        initWidget(ui.asWidget());

        userPermission = UserCache.getPermission().getModule("w_notify");
        if (userPermission == null) {
            Window.alert(Messages.get().error_screenPerm("Email Notification Screen"));
            return;
        }

        initialize();
        setState(QUERY);
    }

    /**
     * Setup state and data change handles for every widget on the screen
     */
    private void initialize() {
    }

}