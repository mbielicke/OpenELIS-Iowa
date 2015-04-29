package org.openelis.portal.modules.emailNotification.client;

import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;

import com.google.gwt.user.client.ui.IsWidget;

public interface EmailNotificationUI extends IsWidget {

    public Table<Row> getTable();

    public Dropdown<Integer> getOrg();

    public Dropdown<String> getFilter();

    public Button getAddButton();

    public Button getRemoveButton();

    public Button getSaveButton();

}