package org.openelis.portal.modules.sampleStatus.client;

import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.MultiDropdown;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.calendar.Calendar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class SampleStatusUIImpl extends ResizeComposite implements SampleStatusUI {

    @UiTemplate("SampleStatus.ui.xml")
    interface SampleStatusUiBinder extends UiBinder<Widget, SampleStatusUIImpl> {
    };

    protected static final SampleStatusUiBinder uiBinder = GWT.create(SampleStatusUiBinder.class);

    @UiField
    protected TextBox<Integer>                  accessionFrom, accessionTo;

    @UiField
    protected TextBox<String>                   clientReference, envCollector, sdwisCollector,
                    pwsId, patientFirst, patientLast;

    @UiField
    protected Calendar                          collectedFrom, collectedTo, releasedFrom,
                    releasedTo, patientBirthFrom, patientBirthTo;

    @UiField
    protected MultiDropdown<Integer>            projectCode;

    @UiField
    protected Button                            getSampleListButton, resetButton, backButton;

    @UiField
    protected FlexTable                         table;

    @UiField
    protected TableCellElement                  sampleCount;

    @UiField
    protected DeckPanel                         deck;

    public SampleStatusUIImpl() {
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
    public MultiDropdown<Integer> getProjectCode() {
        return projectCode;
    }

    @Override
    public Button getGetSampleListButton() {
        return getSampleListButton;
    }

    @Override
    public Button getResetButton() {
        return resetButton;
    }

    @Override
    public Button getBackButton() {
        return backButton;
    }

    @Override
    public FlexTable getTable() {
        return table;
    }

    @Override
    public TableCellElement getSampleCountText() {
        return sampleCount;
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