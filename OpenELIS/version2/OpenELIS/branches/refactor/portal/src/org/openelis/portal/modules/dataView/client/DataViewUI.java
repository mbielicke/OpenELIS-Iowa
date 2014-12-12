package org.openelis.portal.modules.dataView.client;

import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.CheckBox;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.calendar.Calendar;
import org.openelis.ui.widget.table.Table;

import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.IsWidget;

public interface DataViewUI extends IsWidget {

    public void initialize();

    public TextBox<Integer> getAccessionFrom();

    public TextBox<Integer> getAccessionTo();

    public TextBox<String> getClientReference();

    public TextBox<String> getCollector();

    public TextBox<String> getCollectionSite();

    public TextBox<String> getCollectionTown();

    public Calendar getCollectedFrom();

    public Calendar getCollectedTo();

    public Calendar getReleasedFrom();

    public Calendar getReleasedTo();

    public Dropdown<Integer> getProjectCode();

    public CheckBox getAccession();

    public CheckBox getSampleCollected();

    public CheckBox getSampleReceived();

    public CheckBox getSampleReleased();

    public CheckBox getSampleStatus();

    public CheckBox getProjectId();

    public CheckBox getClientReferenceHeader();

    public CheckBox getCollectorHeader();

    public CheckBox getCollectionSiteHeader();

    public CheckBox getSampleDescription();

    public CheckBox getCollectorPhone();

    public CheckBox getSampleType();

    public CheckBox getSource();

    public CheckBox getSampleLocationCity();

    public CheckBox getOrganizationName();

    public CheckBox getOrganizationApt();

    public CheckBox getOrganizationAddress();

    public CheckBox getOrganizationCity();

    public CheckBox getOrganizationState();

    public CheckBox getOrganizationZip();

    public CheckBox getAnalysisTest();

    public CheckBox getAnalysisMethod();

    public CheckBox getAnalysisRevision();

    public CheckBox getAnalysisUnit();

    public CheckBox getAnalysisStarted();

    public CheckBox getAnalysisCompleted();

    public CheckBox getAnalysisReleased();

    public CheckBox getAnalysisQa();

    public CheckBox getPatientFirstName();

    public CheckBox getPatientLastName();

    public CheckBox getPatientBirth();

    public CheckBox getPatientGender();

    public CheckBox getPatientRace();

    public CheckBox getPatientEthnicity();

    public Table getAnalyteTable();

    public Table getAuxTable();

    public Button getContinueButton();

    public Button getResetButton();

    public Button getBackButton();

    public Button getSelectAllSampleFieldsButton();

    public Button getSelectAllOrgFieldsButton();

    public Button getSelectAllAnalysisFieldsButton();

    public Button getSelectAllPatientFieldsButton();

    public Button getSelectAllAnalytesButton();

    public Button getUnselectAllAnalytesButton();

    public Button getSelectAllAuxButton();

    public Button getUnselectAllAuxButton();

    public Button getRunReportButton();

    public DeckPanel getDeck();

    public void setCollectedError(String error);

    public void setReleasedError(String error);

    public void setAccessionError(String error);

    public void setCollectorError(String error);

    public void setClientReferenceError(String error);

    public void setCollectionSiteError(String error);

    public void setCollectionTownError(String error);

    public void setProjectError(String error);

    public void clearErrors();
}