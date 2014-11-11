package org.openelis.portal.modules.emailNotification.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class EmailNotificationUITabletImpl extends ResizeComposite implements EmailNotificationUI {

    @UiTemplate("EmailNotificationTablet.ui.xml")
    interface EmailNotificationUiBinder extends UiBinder<Widget, EmailNotificationUITabletImpl> {
    };

    protected static final EmailNotificationUiBinder uiBinder = GWT.create(EmailNotificationUiBinder.class);

    public EmailNotificationUITabletImpl() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public Widget asWidget() {
        return this;
    }
}
