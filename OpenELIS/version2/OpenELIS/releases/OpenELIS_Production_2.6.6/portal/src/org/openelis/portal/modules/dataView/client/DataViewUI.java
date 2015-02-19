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

    public Dropdown<Integer> getProjectCode();

    public CheckBox getAccession();

    public CheckBox getSampleCollected();

    public CheckBox getSampleReceived();

    public CheckBox getSampleReleased();

    public CheckBox getSampleStatus();

    public CheckBox getProjectId();

    public CheckBox getClientReferenceHeader();

    public CheckBox getSampleType();

    public CheckBox getSource();

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

    public CheckBox getPwsIdHeader();

    public CheckBox getPwsName();

    public CheckBox getSdwisCollectorHeader();

    public CheckBox getSdwisLocation();

    public CheckBox getFacilityId();

    public CheckBox getSdwisSampleType();

    public CheckBox getSampleCategory();

    public CheckBox getSamplePointId();

    public CheckBox getEnvCollectorHeader();

    public CheckBox getEnvLocation();

    public CheckBox getEnvLocationCity();

    public CheckBox getCollectorPhone();

    public CheckBox getSampleDescription();

    public Table getAnalyteTable();

    public Table getAuxTable();

    public Button getContinueButton();

    public Button getResetButton();

    public Button getBackButton();

    public Button getSelectAllSampleFieldsButton();

    public Button getSelectAllOrgFieldsButton();

    public Button getSelectAllAnalysisFieldsButton();

    public Button getSelectAllPatientFieldsButton();

    public Button getSelectAllSdwisFieldsButton();

    public Button getSelectAllEnvironmentalFieldsButton();

    public Button getSelectAllAnalytesButton();

    public Button getUnselectAllAnalytesButton();

    public Button getSelectAllAuxButton();

    public Button getUnselectAllAuxButton();

    public Button getRunReportButton();

    public DeckPanel getDeck();

    public void setCollectedError(String error);

    public void setReleasedError(String error);

    public void setAccessionError(String error);

    public void setClientReferenceError(String error);

    public void setProjectError(String error);

    public void setEnvCollectorError(String error);

    public void setSdwisCollectorError(String error);

    public void setPwsError(String error);

    public void setPatientFirstError(String error);

    public void setPatientLastError(String error);

    public void setPatientBirthError(String error);

    public void clearErrors();
}