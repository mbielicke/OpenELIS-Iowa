package org.openelis.portal.modules.finalReport.client;

import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.MultiDropdown;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.calendar.Calendar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class FinalReportUIImpl extends ResizeComposite implements FinalReportUI {

    @UiTemplate("FinalReport.ui.xml")
    interface FinalReportUiBinder extends UiBinder<Widget, FinalReportUIImpl> {
    };

    protected static final FinalReportUiBinder uiBinder = GWT.create(FinalReportUiBinder.class);

    @UiField
    protected TextBox<Integer>                 accessionFrom, accessionTo;

    @UiField
    protected TextBox<String>                  clientReference, envCollector, sdwisCollector,
                    pwsId, patientFirst, patientLast;

    @UiField
    protected Calendar                         collectedFrom, collectedTo, releasedFrom,
                    releasedTo, patientBirthFrom, patientBirthTo;

    @UiField
    protected MultiDropdown<Integer>                projectCode;

    @UiField
    protected Button                           getSampleListButton, resetButton, backButton,
                    selectAllButton, unselectAllButton, runReportButton;

    @UiField
    protected FlexTable                        table;

    @UiField
    protected DeckLayoutPanel                  deck;

    public FinalReportUIImpl() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    public TextBox<Integer> getAccessionFrom() {
        return accessionFrom;
    }

    public TextBox<Integer> getAccessionTo() {
        return accessionTo;
    }

    public TextBox<String> getPwsId() {
        return pwsId;
    }

    public TextBox<String> getClientReference() {
        return clientReference;
    }

    public TextBox<String> getEnvCollector() {
        return envCollector;
    }

    public TextBox<String> getSdwisCollector() {
        return sdwisCollector;
    }

    public TextBox<String> getPatientFirst() {
        return patientFirst;
    }

    public TextBox<String> getPatientLast() {
        return patientLast;
    }

    public Calendar getCollectedFrom() {
        return collectedFrom;
    }

    public Calendar getCollectedTo() {
        return collectedTo;
    }

    public Calendar getReleasedFrom() {
        return releasedFrom;
    }

    public Calendar getReleasedTo() {
        return releasedTo;
    }

    public Calendar getPatientBirthFrom() {
        return patientBirthFrom;
    }

    public Calendar getPatientBirthTo() {
        return patientBirthTo;
    }

    public MultiDropdown<Integer> getProjectCode() {
        return projectCode;
    }

    public Button getGetSampleListButton() {
        return getSampleListButton;
    }

    public Button getResetButton() {
        return resetButton;
    }

    public Button getBackButton() {
        return backButton;
    }

    public Button getSelectAllButton() {
        return selectAllButton;
    }

    public Button getUnselectAllButton() {
        return unselectAllButton;
    }

    public Button getRunReportButton() {
        return runReportButton;
    }

    public FlexTable getTable() {
        return table;
    }

    public DeckLayoutPanel getDeck() {
        return deck;
    }
}