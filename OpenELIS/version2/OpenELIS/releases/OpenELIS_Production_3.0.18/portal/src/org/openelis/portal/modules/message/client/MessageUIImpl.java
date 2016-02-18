package org.openelis.portal.modules.message.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class MessageUIImpl extends ResizeComposite implements MessageUI {

    @UiTemplate("Message.ui.xml")
    interface MessageUiBinder extends UiBinder<Widget, MessageUIImpl> {
    };

    @UiField
    protected HTML                         message;

    protected static final MessageUiBinder uiBinder = GWT.create(MessageUiBinder.class);

    public MessageUIImpl() {
        initWidget(uiBinder.createAndBindUi(this));
        initialize();
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    @Override
    public void initialize() {
    }

    @Override
    public HTML getMessage() {
        return message;
    }

    @Override
    public void setMessage(String html) {
        message.setHTML(html);
    }

}