package org.openelis.portal.modules.finalReport.client;

import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Dropdown;
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
    protected Dropdown<Integer>                projectCode;

    @UiField
    protected Button                           getSampleListButton, resetButton, backButton;

    @UiField
    protected FlexTable                            table;

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

    public void setAccessionFrom(TextBox<Integer> accessionFrom) {
        this.accessionFrom = accessionFrom;
    }

    public TextBox<Integer> getAccessionTo() {
        return accessionTo;
    }

    public void setAccessionTo(TextBox<Integer> accessionTo) {
        this.accessionTo = accessionTo;
    }

    public TextBox<String> getPwsId() {
        return pwsId;
    }

    public void setPwsId(TextBox<String> pwsId) {
        this.pwsId = pwsId;
    }

    public TextBox<String> getClientReference() {
        return clientReference;
    }

    public void setClientReference(TextBox<String> clientReference) {
        this.clientReference = clientReference;
    }

    public TextBox<String> getEnvCollector() {
        return envCollector;
    }

    public void setEnvCollector(TextBox<String> envCollector) {
        this.envCollector = envCollector;
    }

    public TextBox<String> getSdwisCollector() {
        return sdwisCollector;
    }

    public void setSdwisCollector(TextBox<String> sdwisCollector) {
        this.sdwisCollector = sdwisCollector;
    }

    public TextBox<String> getPatientFirst() {
        return patientFirst;
    }

    public void setPatientFirst(TextBox<String> patientFirst) {
        this.patientFirst = patientFirst;
    }

    public TextBox<String> getPatientLast() {
        return patientLast;
    }

    public void setPatientLast(TextBox<String> patientLast) {
        this.patientLast = patientLast;
    }

    public Calendar getCollectedFrom() {
        return collectedFrom;
    }

    public void setCollectedFrom(Calendar collectedFrom) {
        this.collectedFrom = collectedFrom;
    }

    public Calendar getCollectedTo() {
        return collectedTo;
    }

    public void setCollectedTo(Calendar collectedTo) {
        this.collectedTo = collectedTo;
    }

    public Calendar getReleasedFrom() {
        return releasedFrom;
    }

    public void setReleasedFrom(Calendar releasedFrom) {
        this.releasedFrom = releasedFrom;
    }

    public Calendar getReleasedTo() {
        return releasedTo;
    }

    public void setReleasedTo(Calendar releasedTo) {
        this.releasedTo = releasedTo;
    }

    public Calendar getPatientBirthFrom() {
        return patientBirthFrom;
    }

    public void setPatientBirthFrom(Calendar patientBirthFrom) {
        this.patientBirthFrom = patientBirthFrom;
    }

    public Calendar getPatientBirthTo() {
        return patientBirthTo;
    }

    public void setPatientBirthTo(Calendar patientBirthTo) {
        this.patientBirthTo = patientBirthTo;
    }

    public Dropdown<Integer> getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(Dropdown<Integer> projectCode) {
        this.projectCode = projectCode;
    }

    public Button getGetSampleListButton() {
        return getSampleListButton;
    }

    public void setGetSampleListButton(Button getSampleListButton) {
        this.getSampleListButton = getSampleListButton;
    }

    public Button getResetButton() {
        return resetButton;
    }

    public void setResetButton(Button resetButton) {
        this.resetButton = resetButton;
    }

    public Button getBackButton() {
        return backButton;
    }

    public void setBackButton(Button backButton) {
        this.backButton = backButton;
    }

    public FlexTable getTable() {
        return table;
    }

    public void setTable(FlexTable table) {
        this.table = table;
    }

    public DeckLayoutPanel getDeck() {
        return deck;
    }

    public void setDeck(DeckLayoutPanel deck) {
        this.deck = deck;
    }
}
