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
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class FinalReportUITabletImpl extends ResizeComposite implements FinalReportUI {

    @UiTemplate("FinalReportTablet.ui.xml")
    interface FinalReportUiBinder extends UiBinder<Widget, FinalReportUITabletImpl> {
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
    protected MultiDropdown<Integer>           projectCode;

    @UiField
    protected Button                           getSampleListButton, resetButton, backButton,
                    selectAllButton, unselectAllButton, runReportButton;

    @UiField
    protected FlexTable                        table;

    @UiField
    protected DeckPanel                        deck;

    public FinalReportUITabletImpl() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    @Override
    public void initialize() {

    }

    @Override
    public void setRowHeight(int i, String height) {
        table.getRowFormatter().getElement(i).setAttribute("height", height);
        table.getCellFormatter().setWidth(i, 0, height);
        table.getWidget(i, 0).setHeight(height);
        table.getWidget(i, 0).setWidth(height);
        table.getCellFormatter().setHorizontalAlignment(i, 0, HasHorizontalAlignment.ALIGN_CENTER);
        table.getCellFormatter().setVerticalAlignment(i, 0, HasVerticalAlignment.ALIGN_MIDDLE);
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
    public Button getSelectAllButton() {
        return selectAllButton;
    }

    @Override
    public Button getUnselectAllButton() {
        return unselectAllButton;
    }

    @Override
    public Button getRunReportButton() {
        return runReportButton;
    }

    @Override
    public FlexTable getTable() {
        return table;
    }

    @Override
    public DeckPanel getDeck() {
        return deck;
    }

}
