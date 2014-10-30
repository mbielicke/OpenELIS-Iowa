package org.openelis.portal.modules.dataView.client;

import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.CheckBox;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Help;
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

public class DataViewUIMobileImpl extends ResizeComposite implements DataViewUI {

    @UiTemplate("DataViewMobile.ui.xml")
    interface DataViewUiBinder extends UiBinder<Widget, DataViewUIMobileImpl> {
    };

    protected static final DataViewUiBinder uiBinder = GWT.create(DataViewUiBinder.class);

    @UiField
    protected TextBox<Integer>              accessionFrom, accessionTo;

    @UiField
    protected TextBox<String>               clientReference, collector, collectionSite,
                    collectionTown;

    @UiField
    protected Calendar                      collectedFrom, collectedTo, releasedFrom, releasedTo;

    @UiField
    protected Dropdown<Integer>             projectCode;

    @UiField
    protected CheckBox                      accession, sampleCollected, sampleReceived,
                    sampleReleased, sampleStatus, projectId, clientReferenceHeader,
                    collectorHeader, collectionSiteHeader, sampleDescription, collectorPhone,
                    sampleType, source, sampleLocationCity, organizationName, organizationApt,
                    organizationAddress, organizationCity, organizationState, organizationZip,
                    analysisTest, analysisMethod, analysisRevision, analysisUnit, analysisStarted,
                    analysisCompleted, analysisReleased, analysisQa, patientLastName,
                    patientFirstName, patientBirth, patientGender, patientRace, patientEthnicity;

    @UiField
    protected Table                         analyteTable, auxTable;

    @UiField
    protected Button                        continueButton, resetButton,
                    selectAllSampleFieldsButton, selectAllOrgFieldsButton,
                    selectAllAnalysisFieldsButton, selectAllPatientFieldsButton,
                    selectAllAnalytesButton, unselectAllAnalytesButton, selectAllAuxButton,
                    unselectAllAuxButton, backButton, runReportButton;

    @UiField
    protected DeckPanel                     deck;

    @UiField
    protected Help                          collectedError, releasedError, accessionError,
                    collectorError, clientReferenceError, collectionSiteError, collectionTownError,
                    projectError;

    public DataViewUIMobileImpl() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    @Override
    public void initialize() {
        collectedFrom.setWidth("75px");
        collectedTo.setWidth("75px");
        releasedFrom.setWidth("75px");
        releasedTo.setWidth("75px");
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
    public TextBox<String> getClientReference() {
        return clientReference;
    }

    @Override
    public TextBox<String> getCollector() {
        return collector;
    }

    @Override
    public TextBox<String> getCollectionSite() {
        return collectionSite;
    }

    @Override
    public TextBox<String> getCollectionTown() {
        return collectionTown;
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
    public CheckBox getCollectorHeader() {
        return collectorHeader;
    }

    @Override
    public CheckBox getCollectionSiteHeader() {
        return collectionSiteHeader;
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
    public CheckBox getSampleLocationCity() {
        return sampleLocationCity;
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
    public Button getSelectAllSampleFieldsButton() {
        return selectAllSampleFieldsButton;
    }

    @Override
    public Button getSelectAllOrgFieldsButton() {
        return selectAllOrgFieldsButton;
    }

    @Override
    public Button getSelectAllAnalysisFieldsButton() {
        return selectAllAnalysisFieldsButton;
    }

    @Override
    public Button getSelectAllPatientFieldsButton() {
        return selectAllPatientFieldsButton;
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
        if (error == null) {
            collectedError.setVisible(false);
        } else {
            collectedError.setText(error);
            collectedError.setVisible(true);
        }
    }

    @Override
    public void setReleasedError(String error) {
        if (error == null) {
            releasedError.setVisible(false);
        } else {
            releasedError.setText(error);
            releasedError.setVisible(true);
        }
    }

    @Override
    public void setAccessionError(String error) {
        if (error == null) {
            accessionError.setVisible(false);
        } else {
            accessionError.setText(error);
            accessionError.setVisible(true);
        }
    }

    @Override
    public void setCollectorError(String error) {
        if (error == null) {
            collectorError.setVisible(false);
        } else {
            collectorError.setText(error);
            collectorError.setVisible(true);
        }
    }

    @Override
    public void setClientReferenceError(String error) {
        if (error == null) {
            clientReferenceError.setVisible(false);
        } else {
            clientReferenceError.setText(error);
            clientReferenceError.setVisible(true);
        }
    }

    @Override
    public void setCollectionSiteError(String error) {
        if (error == null) {
            collectionSiteError.setVisible(false);
        } else {
            collectionSiteError.setText(error);
            collectionSiteError.setVisible(true);
        }
    }

    @Override
    public void setCollectionTownError(String error) {
        if (error == null) {
            collectionTownError.setVisible(false);
        } else {
            collectionTownError.setText(error);
            collectionTownError.setVisible(true);
        }
    }

    @Override
    public void setProjectError(String error) {
        if (error == null) {
            projectError.setVisible(false);
        } else {
            projectError.setText(error);
            projectError.setVisible(true);
        }
    }

    @Override
    public void clearErrors() {
        collectedError.setVisible(false);
        releasedError.setVisible(false);
        accessionError.setVisible(false);
        collectorError.setVisible(false);
        clientReferenceError.setVisible(false);
        collectionSiteError.setVisible(false);
        collectionTownError.setVisible(false);
        projectError.setVisible(false);
    }
}
