package org.openelis.portal.modules.finalReport.client;

import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.calendar.Calendar;

import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.IsWidget;

public interface FinalReportUI extends IsWidget {

    public TextBox<Integer> getAccessionFrom();

    public void setAccessionFrom(TextBox<Integer> accessionFrom);

    public TextBox<Integer> getAccessionTo();

    public void setAccessionTo(TextBox<Integer> accessionTo);

    public TextBox<String> getPwsId();

    public void setPwsId(TextBox<String> pwsId);

    public TextBox<String> getClientReference();

    public void setClientReference(TextBox<String> clientReference);

    public TextBox<String> getEnvCollector();

    public void setEnvCollector(TextBox<String> envCollector);

    public TextBox<String> getSdwisCollector();

    public void setSdwisCollector(TextBox<String> sdwisCollector);

    public TextBox<String> getPatientFirst();

    public void setPatientFirst(TextBox<String> patientFirst);

    public TextBox<String> getPatientLast();

    public void setPatientLast(TextBox<String> patientLast);

    public Calendar getCollectedFrom();

    public void setCollectedFrom(Calendar collectedFrom);

    public Calendar getCollectedTo();

    public void setCollectedTo(Calendar collectedTo);

    public Calendar getReleasedFrom();

    public void setReleasedFrom(Calendar releasedFrom);

    public Calendar getReleasedTo();

    public void setReleasedTo(Calendar releasedTo);

    public Calendar getPatientBirthFrom();

    public void setPatientBirthFrom(Calendar patientBirthFrom);

    public Calendar getPatientBirthTo();

    public void setPatientBirthTo(Calendar patientBirthTo);

    public Dropdown<Integer> getProjectCode();

    public void setProjectCode(Dropdown<Integer> projectCode);

    public Button getGetSampleListButton();

    public void setGetSampleListButton(Button getSampleListButton);

    public Button getResetButton();

    public void setResetButton(Button resetButton);

    public Button getBackButton();

    public void setBackButton(Button backButton);

    public FlexTable getTable();

    public void setTable(FlexTable table);

    public DeckLayoutPanel getDeck();

    public void setDeck(DeckLayoutPanel deck);
}
