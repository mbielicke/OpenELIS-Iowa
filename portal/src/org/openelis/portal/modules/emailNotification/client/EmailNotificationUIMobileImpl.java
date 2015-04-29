package org.openelis.portal.modules.emailNotification.client;

import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class EmailNotificationUIMobileImpl extends ResizeComposite implements EmailNotificationUI {

    @UiTemplate("EmailNotificationMobile.ui.xml")
    interface EmailNotificationUiBinder extends UiBinder<Widget, EmailNotificationUIMobileImpl> {
    };

    protected static final EmailNotificationUiBinder uiBinder = GWT.create(EmailNotificationUiBinder.class);

    @UiField
    protected Table<Row>                            table;

    @UiField
    protected Dropdown<Integer>                      org;

    @UiField
    protected Dropdown<String>                       filter;

    @UiField
    protected Button                                 addButton, removeButton, saveButton;

    public EmailNotificationUIMobileImpl() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    @Override
    public Table getTable() {
        return table;
    }

    @Override
    public Dropdown<Integer> getOrg() {
        return org;
    }

    @Override
    public Dropdown<String> getFilter() {
        return filter;
    }

    @Override
    public Button getAddButton() {
        return addButton;
    }

    @Override
    public Button getRemoveButton() {
        return removeButton;
    }

    @Override
    public Button getSaveButton() {
        return saveButton;
    }
}
