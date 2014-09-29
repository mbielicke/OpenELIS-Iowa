package org.openelis.portal.modules.sampleStatus.client;

import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.MultiDropdown;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.calendar.Calendar;

import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.IsWidget;

public interface SampleStatusUI extends IsWidget {

    public void initialize();
    
    public TextBox<Integer> getAccessionFrom();

    public TextBox<Integer> getAccessionTo();

    public TextBox<String> getClientReference();

    public Calendar getCollectedFrom();

    public Calendar getCollectedTo();

    public MultiDropdown<Integer> getProjectCode();

    public Button getGetSampleListButton();

    public Button getResetButton();

    public Button getBackButton();

    public Button getSelectAllButton();

    public Button getUnselectAllButton();

    public Button getRunReportButton();

    public FlexTable getTable();

    public DeckLayoutPanel getDeck();

}