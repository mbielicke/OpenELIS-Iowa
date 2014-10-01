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
}