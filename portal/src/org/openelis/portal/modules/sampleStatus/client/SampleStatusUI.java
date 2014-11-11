package org.openelis.portal.modules.sampleStatus.client;

import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.MultiDropdown;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.calendar.Calendar;

import com.google.gwt.user.client.ui.DeckPanel;
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

    public FlexTable getTable();

    public DeckPanel getDeck();

    public void setCollectedError(String error);

    public void setAccessionError(String error);

    public void setClientReferenceError(String error);

    public void setProjectError(String error);

    public void clearErrors();

}