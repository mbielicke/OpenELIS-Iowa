package org.openelis.portal.modules.sampleStatus.client;

import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Help;
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

public class SampleStatusUIMobileImpl extends ResizeComposite implements SampleStatusUI {

    @UiTemplate("SampleStatusMobile.ui.xml")
    interface SampleStatusUiBinder extends UiBinder<Widget, SampleStatusUIMobileImpl> {
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

    @UiField
    protected Help                              collectedError, releasedError, accessionError,
                    clientReferenceError, projectError, envCollectorError, sdwisCollectorError,
                    pwsError, patientFirstError, patientLastError, patientBirthError;

    public SampleStatusUIMobileImpl() {
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
        patientBirthFrom.setWidth("75px");
        patientBirthTo.setWidth("75px");
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
    public void setClientReferenceError(String error) {
        if (error == null) {
            clientReferenceError.setVisible(false);
        } else {
            clientReferenceError.setText(error);
            clientReferenceError.setVisible(true);
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
    public void setEnvCollectorError(String error) {
        if (error == null) {
            envCollectorError.setVisible(false);
        } else {
            envCollectorError.setText(error);
            envCollectorError.setVisible(true);
        }
    }

    @Override
    public void setSdwisCollectorError(String error) {
        if (error == null) {
            sdwisCollectorError.setVisible(false);
        } else {
            sdwisCollectorError.setText(error);
            sdwisCollectorError.setVisible(true);
        }
    }

    @Override
    public void setPwsError(String error) {
        if (error == null) {
            pwsError.setVisible(false);
        } else {
            pwsError.setText(error);
            pwsError.setVisible(true);
        }
    }

    @Override
    public void setPatientFirstError(String error) {
        if (error == null) {
            patientFirstError.setVisible(false);
        } else {
            patientFirstError.setText(error);
            patientFirstError.setVisible(true);
        }
    }

    @Override
    public void setPatientLastError(String error) {
        if (error == null) {
            patientLastError.setVisible(false);
        } else {
            patientLastError.setText(error);
            patientLastError.setVisible(true);
        }
    }

    @Override
    public void setPatientBirthError(String error) {
        if (error == null) {
            patientBirthError.setVisible(false);
        } else {
            patientBirthError.setText(error);
            patientBirthError.setVisible(true);
        }
    }

    @Override
    public void clearErrors() {
        collectedError.setVisible(false);
        releasedError.setVisible(false);
        accessionError.setVisible(false);
        clientReferenceError.setVisible(false);
        projectError.setVisible(false);
        envCollectorError.setVisible(false);
        sdwisCollectorError.setVisible(false);
        pwsError.setVisible(false);
        patientFirstError.setVisible(false);
        patientLastError.setVisible(false);
        patientBirthError.setVisible(false);
    }
}
