package org.openelis.portal.modules.dataView.client;

import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.CheckBox;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.calendar.Calendar;
import org.openelis.ui.widget.table.Table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class DataViewUIImpl extends ResizeComposite implements DataViewUI {

    @UiTemplate("DataView.ui.xml")
    interface DataViewUiBinder extends UiBinder<Widget, DataViewUIImpl> {
    };

    protected static final DataViewUiBinder uiBinder = GWT.create(DataViewUiBinder.class);

    @UiField
    protected TextBox<Integer>              accessionFrom, accessionTo;

    @UiField
    protected TextBox<String>               clientReference, envCollector, sdwisCollector, pwsId,
                    patientFirst, patientLast;

    @UiField
    protected Calendar                      collectedFrom, collectedTo, releasedFrom, releasedTo,
                    patientBirthFrom, patientBirthTo;

    @UiField
    protected Dropdown<Integer>             projectCode;

    @UiField
    protected CheckBox                      accession, sampleCollected, sampleReceived,
                    sampleReleased, sampleStatus, projectId, clientReferenceHeader, sampleType,
                    source, organizationName, organizationApt, organizationAddress,
                    organizationCity, organizationState, organizationZip, analysisTest,
                    analysisMethod, analysisRevision, analysisUnit, analysisStarted,
                    analysisCompleted, analysisReleased, analysisQa, patientLastName,
                    patientFirstName, patientBirth, patientGender, patientRace, patientEthnicity,
                    pwsIdHeader, pwsName, sdwisCollectorHeader, sdwisLocation, facilityId,
                    sdwisSampleType, sampleCategory, samplePointId, envCollectorHeader,
                    envLocation, envLocationCity, collectorPhone, sampleDescription;

    @UiField
    protected Table                         analyteTable, auxTable;

    @UiField
    protected Button                        continueButton, resetButton, selectAllAnalytesButton,
                    unselectAllAnalytesButton, selectAllAuxButton, unselectAllAuxButton,
                    backButton, runReportButton;

    @UiField
    protected DeckPanel                     deck;

    public DataViewUIImpl() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    @Override
    public void initialize() {
        collectedFrom.setWidth("105px");
        collectedTo.setWidth("105px");
        releasedFrom.setWidth("105px");
        releasedTo.setWidth("105px");
        patientBirthFrom.setWidth("105px");
        patientBirthTo.setWidth("105px");
    }

    @Override
    public TextBox<Integer> getAccessionFrom() {
        return accessionFrom;
    }

    @Override
    public TextBox<Integer> getAccessionTo() {
        return accessionTo;
    }

    @Override
    public TextBox<String> getPwsId() {
        return pwsId;
    }

    @Override
    public TextBox<String> getClientReference() {
        return clientReference;
    }

    @Override
    public TextBox<String> getEnvCollector() {
        return envCollector;
    }

    @Override
    public TextBox<String> getSdwisCollector() {
        return sdwisCollector;
    }

    @Override
    public TextBox<String> getPatientFirst() {
        return patientFirst;
    }

    @Override
    public TextBox<String> getPatientLast() {
        return patientLast;
    }

    @Override
    public Calendar getCollectedFrom() {
        return collectedFrom;
    }

    @Override
    public Calendar getCollectedTo() {
        return collectedTo;
    }

    @Override
    public Calendar getReleasedFrom() {
        return releasedFrom;
    }

    @Override
    public Calendar getReleasedTo() {
        return releasedTo;
    }

    @Override
    public Calendar getPatientBirthFrom() {
        return patientBirthFrom;
    }

    @Override
    public Calendar getPatientBirthTo() {
        return patientBirthTo;
    }

    @Override
    public Dropdown<Integer> getProjectCode() {
        return projectCode;
    }

    @Override
    public CheckBox getAccession() {
        return accession;
    }

    @Override
    public CheckBox getSampleCollected() {
        return sampleCollected;
    }

    @Override
    public CheckBox getSampleReceived() {
        return sampleReceived;
    }

    @Override
    public CheckBox getSampleReleased() {
        return sampleReleased;
    }

    @Override
    public CheckBox getSampleStatus() {
        return sampleStatus;
    }

    @Override
    public CheckBox getProjectId() {
        return projectId;
    }

    @Override
    public CheckBox getClientReferenceHeader() {
        return clientReferenceHeader;
    }

    @Override
    public CheckBox getSampleDescription() {
        return sampleDescription;
    }

    @Override
    public CheckBox getCollectorPhone() {
        return collectorPhone;
    }

    @Override
    public CheckBox getSampleType() {
        return sampleType;
    }

    @Override
    public CheckBox getSource() {
        return source;
    }

    @Override
    public CheckBox getOrganizationName() {
        return organizationName;
    }

    @Override
    public CheckBox getOrganizationApt() {
        return organizationApt;
    }

    @Override
    public CheckBox getOrganizationAddress() {
        return organizationAddress;
    }

    @Override
    public CheckBox getOrganizationCity() {
        return organizationCity;
    }

    @Override
    public CheckBox getOrganizationState() {
        return organizationState;
    }

    @Override
    public CheckBox getOrganizationZip() {
        return organizationZip;
    }

    @Override
    public CheckBox getAnalysisTest() {
        return analysisTest;
    }

    @Override
    public CheckBox getAnalysisMethod() {
        return analysisMethod;
    }

    @Override
    public CheckBox getAnalysisRevision() {
        return analysisRevision;
    }

    @Override
    public CheckBox getAnalysisUnit() {
        return analysisUnit;
    }

    @Override
    public CheckBox getAnalysisStarted() {
        return analysisStarted;
    }

    @Override
    public CheckBox getAnalysisCompleted() {
        return analysisCompleted;
    }

    @Override
    public CheckBox getAnalysisReleased() {
        return analysisReleased;
    }

    @Override
    public CheckBox getAnalysisQa() {
        return analysisQa;
    }

    @Override
    public CheckBox getPatientFirstName() {
        return patientFirstName;
    }

    @Override
    public CheckBox getPatientLastName() {
        return patientLastName;
    }

    @Override
    public CheckBox getPatientBirth() {
        return patientBirth;
    }

    @Override
    public CheckBox getPatientGender() {
        return patientGender;
    }

    @Override
    public CheckBox getPatientRace() {
        return patientRace;
    }

    @Override
    public CheckBox getPatientEthnicity() {
        return patientEthnicity;
    }

    @Override
    public CheckBox getPwsIdHeader() {
        return pwsIdHeader;
    }

    @Override
    public CheckBox getPwsName() {
        return pwsName;
    }

    @Override
    public CheckBox getSdwisCollectorHeader() {
        return sdwisCollectorHeader;
    }

    @Override
    public CheckBox getSdwisLocation() {
        return sdwisLocation;
    }

    @Override
    public CheckBox getFacilityId() {
        return facilityId;
    }

    @Override
    public CheckBox getSdwisSampleType() {
        return sdwisSampleType;
    }

    @Override
    public CheckBox getSampleCategory() {
        return sampleCategory;
    }

    @Override
    public CheckBox getSamplePointId() {
        return samplePointId;
    }

    @Override
    public CheckBox getEnvCollectorHeader() {
        return envCollectorHeader;
    }

    @Override
    public CheckBox getEnvLocation() {
        return envLocation;
    }

    @Override
    public CheckBox getEnvLocationCity() {
        return envLocationCity;
    }

    @Override
    public Table getAnalyteTable() {
        return analyteTable;
    }

    @Override
    public Table getAuxTable() {
        return auxTable;
    }

    @Override
    public Button getContinueButton() {
        return continueButton;
    }

    @Override
    public Button getResetButton() {
        return resetButton;
    }

    @Override
    public Button getSelectAllAnalytesButton() {
        return selectAllAnalytesButton;
    }

    @Override
    public Button getUnselectAllAnalytesButton() {
        return unselectAllAnalytesButton;
    }

    @Override
    public Button getSelectAllAuxButton() {
        return selectAllAuxButton;
    }

    @Override
    public Button getUnselectAllAuxButton() {
        return unselectAllAuxButton;
    }

    @Override
    public Button getBackButton() {
        return backButton;
    }

    @Override
    public Button getRunReportButton() {
        return runReportButton;
    }

    @Override
    public DeckPanel getDeck() {
        return deck;
    }

    @Override
    public void setCollectedError(String error) {
        collectedFrom.addException(new Exception(error));
        collectedTo.addException(new Exception(error));
    }

    @Override
    public void setReleasedError(String error) {
        releasedFrom.addException(new Exception(error));
        releasedTo.addException(new Exception(error));
    }

    @Override
    public void setAccessionError(String error) {
        accessionFrom.addException(new Exception(error));
        accessionTo.addException(new Exception(error));
    }

    @Override
    public void setClientReferenceError(String error) {
        clientReference.addException(new Exception(error));
    }

    @Override
    public void setProjectError(String error) {
        projectCode.addException(new Exception(error));
    }

    @Override
    public void setEnvCollectorError(String error) {
        envCollector.addException(new Exception(error));
    }

    @Override
    public void setSdwisCollectorError(String error) {
        sdwisCollector.addException(new Exception(error));
    }

    @Override
    public void setPwsError(String error) {
        pwsId.addException(new Exception(error));
    }

    @Override
    public void setPatientFirstError(String error) {
        patientFirst.addException(new Exception(error));
    }

    @Override
    public void setPatientLastError(String error) {
        patientLast.addException(new Exception(error));
    }

    @Override
    public void setPatientBirthError(String error) {
        patientBirthFrom.addException(new Exception(error));
        patientBirthTo.addException(new Exception(error));
    }

    @Override
    public void clearErrors() {
        collectedFrom.clearExceptions();
        collectedTo.clearExceptions();
        releasedFrom.clearExceptions();
        releasedTo.clearExceptions();
        accessionFrom.clearExceptions();
        accessionTo.clearExceptions();
        clientReference.clearExceptions();
        projectCode.clearExceptions();
        envCollector.clearExceptions();
        sdwisCollector.clearExceptions();
        pwsId.clearExceptions();
        patientFirst.clearExceptions();
        patientLast.clearExceptions();
        patientBirthFrom.clearExceptions();
        patientBirthTo.clearExceptions();
    }
}