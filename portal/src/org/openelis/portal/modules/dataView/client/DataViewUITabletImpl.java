package org.openelis.portal.modules.dataView.client;

import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.MultiDropdown;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.calendar.Calendar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class DataViewUITabletImpl extends ResizeComposite implements DataViewUI {

    @UiTemplate("DataViewTablet.ui.xml")
    interface DataViewUiBinder extends UiBinder<Widget, DataViewUITabletImpl> {
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
    protected MultiDropdown<Integer>        projectCode;

    @UiField
    protected Button                        continueButton, resetButton, backButton;

    // @UiField
    // protected FlexTable table;

    @UiField
    protected DeckPanel                     deck;

    public DataViewUITabletImpl() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    @Override
    public void initialize() {
        /*
         * do nothing for tablet
         */
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
    public MultiDropdown<Integer> getProjectCode() {
        return projectCode;
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
    public Button getBackButton() {
        return backButton;
    }

    @Override
    public DeckPanel getDeck() {
        return deck;
    }
}
