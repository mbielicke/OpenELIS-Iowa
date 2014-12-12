package org.openelis.portal.modules.sampleStatus.client;

import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.MultiDropdown;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.calendar.Calendar;

import com.google.gwt.core.client.GWT;
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
    protected TextBox<String>                   clientReference;

    @UiField
    protected Calendar                          collectedFrom, collectedTo;

    @UiField
    protected MultiDropdown<Integer>            projectCode;

    @UiField
    protected Button                            getSampleListButton, resetButton, backButton;

    @UiField
    protected FlexTable                         table;

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
        table.setWidth("250%");
        table.getColumnFormatter().setWidth(0, "75px");
        table.getColumnFormatter().setWidth(1, "25%");
        table.getColumnFormatter().setWidth(2, "6%");
        table.getColumnFormatter().setWidth(3, "8%");
        table.getColumnFormatter().setWidth(4, "8%");
        table.getColumnFormatter().setWidth(5, "8%");
        table.getColumnFormatter().setWidth(6, "40%");
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
    public Calendar getCollectedFrom() {
        return collectedFrom;
    }

    @Override
    public Calendar getCollectedTo() {
        return collectedTo;
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
    public DeckPanel getDeck() {
        return deck;
    }

    @Override
    public void setCollectedError(String error) {
        collectedFrom.addException(new Exception(error));
        collectedTo.addException(new Exception(error));
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
    public void clearErrors() {
        collectedFrom.clearExceptions();
        collectedTo.clearExceptions();
        accessionFrom.clearExceptions();
        accessionTo.clearExceptions();
        clientReference.clearExceptions();
        projectCode.clearExceptions();
    }
}