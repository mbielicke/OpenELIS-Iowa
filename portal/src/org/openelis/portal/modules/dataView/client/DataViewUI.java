package org.openelis.portal.modules.dataView.client;

import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.MultiDropdown;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.calendar.Calendar;

import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.IsWidget;

public interface DataViewUI extends IsWidget {

    public void initialize();
    
    public TextBox<Integer> getAccessionFrom();

    public TextBox<Integer> getAccessionTo();

    public TextBox<String> getClientReference();

    public TextBox<String> getCollector();

    public TextBox<String> getCollectionSite();

    public TextBox<String> getCollectionTown();

    public Calendar getCollectedFrom();

    public Calendar getCollectedTo();

    public Calendar getReleasedFrom();

    public Calendar getReleasedTo();

    public MultiDropdown<Integer> getProjectCode();

    public Button getContinueButton();

    public Button getResetButton();

    public Button getBackButton();

    public DeckPanel getDeck();
}