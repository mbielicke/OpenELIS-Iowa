package org.openelis.portal.modules.finalReport.client;

import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.calendar.Calendar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class FinalReportUIMobileImpl extends ResizeComposite implements FinalReportUI {

    @UiTemplate("FinalReportMobile.ui.xml")
    interface FinalReportUiBinder extends UiBinder<Widget, FinalReportUIMobileImpl> {
    };

    protected static final FinalReportUiBinder uiBinder = GWT.create(FinalReportUiBinder.class);

    public FinalReportUIMobileImpl() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    @Override
    public TextBox<Integer> getAccessionFrom() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setAccessionFrom(TextBox<Integer> accessionFrom) {
        // TODO Auto-generated method stub

    }

    @Override
    public TextBox<Integer> getAccessionTo() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setAccessionTo(TextBox<Integer> accessionTo) {
        // TODO Auto-generated method stub

    }

    @Override
    public TextBox<String> getPwsId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setPwsId(TextBox<String> pwsId) {
        // TODO Auto-generated method stub

    }

    @Override
    public TextBox<String> getClientReference() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setClientReference(TextBox<String> clientReference) {
        // TODO Auto-generated method stub

    }

    @Override
    public TextBox<String> getEnvCollector() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setEnvCollector(TextBox<String> envCollector) {
        // TODO Auto-generated method stub

    }

    @Override
    public TextBox<String> getSdwisCollector() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setSdwisCollector(TextBox<String> sdwisCollector) {
        // TODO Auto-generated method stub

    }

    @Override
    public TextBox<String> getPatientFirst() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setPatientFirst(TextBox<String> patientFirst) {
        // TODO Auto-generated method stub

    }

    @Override
    public TextBox<String> getPatientLast() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setPatientLast(TextBox<String> patientLast) {
        // TODO Auto-generated method stub

    }

    @Override
    public Calendar getCollectedFrom() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setCollectedFrom(Calendar collectedFrom) {
        // TODO Auto-generated method stub

    }

    @Override
    public Calendar getCollectedTo() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setCollectedTo(Calendar collectedTo) {
        // TODO Auto-generated method stub

    }

    @Override
    public Calendar getReleasedFrom() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setReleasedFrom(Calendar releasedFrom) {
        // TODO Auto-generated method stub

    }

    @Override
    public Calendar getReleasedTo() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setReleasedTo(Calendar releasedTo) {
        // TODO Auto-generated method stub

    }

    @Override
    public Calendar getPatientBirthFrom() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setPatientBirthFrom(Calendar patientBirthFrom) {
        // TODO Auto-generated method stub

    }

    @Override
    public Calendar getPatientBirthTo() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setPatientBirthTo(Calendar patientBirthTo) {
        // TODO Auto-generated method stub

    }

    @Override
    public Dropdown<Integer> getProjectCode() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setProjectCode(Dropdown<Integer> projectCode) {
        // TODO Auto-generated method stub

    }

    @Override
    public Button getGetSampleListButton() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setGetSampleListButton(Button getSampleListButton) {
        // TODO Auto-generated method stub

    }

    @Override
    public Button getResetButton() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setResetButton(Button resetButton) {
        // TODO Auto-generated method stub

    }

    @Override
    public Button getBackButton() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setBackButton(Button backButton) {
        // TODO Auto-generated method stub

    }

    @Override
    public FlexTable getTable() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setTable(FlexTable table) {
        // TODO Auto-generated method stub

    }

    @Override
    public DeckLayoutPanel getDeck() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setDeck(DeckLayoutPanel deck) {
        // TODO Auto-generated method stub

    }
}
