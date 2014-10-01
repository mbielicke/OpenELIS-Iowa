package org.openelis.portal.modules.finalReport.client;

import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.MultiDropdown;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.calendar.Calendar;

import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.IsWidget;

public interface FinalReportUI extends IsWidget {

    public void initialize();

    public void setRowHeight(int i, String height);
    
    public TextBox<Integer> getAccessionFrom();

    public TextBox<Integer> getAccessionTo();

    public TextBox<String> getPwsId();

    public TextBox<String> getClientReference();

    public TextBox<String> getEnvCollector();

    public TextBox<String> getSdwisCollector();

    public TextBox<String> getPatientFirst();

    public TextBox<String> getPatientLast();

    public Calendar getCollectedFrom();

    public Calendar getCollectedTo();

    public Calendar getReleasedFrom();

    public Calendar getReleasedTo();

    public Calendar getPatientBirthFrom();

    public Calendar getPatientBirthTo();

    public MultiDropdown<Integer> getProjectCode();

    public Button getGetSampleListButton();

    public Button getResetButton();

    public Button getBackButton();

    public Button getSelectAllButton();

    public Button getUnselectAllButton();

    public Button getRunReportButton();

    public FlexTable getTable();

    public DeckPanel getDeck();
    
}