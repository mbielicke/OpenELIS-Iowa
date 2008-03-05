package org.openelis.modules.utilities.client;

import com.google.gwt.user.client.ui.Widget;

import org.openelis.gwt.screen.AppModule;
import org.openelis.gwt.screen.ScreenBase;
import org.openelis.gwt.screen.ScreenWidget;
import org.openelis.modules.main.client.openelis.OpenELIS;
import org.openelis.modules.utilities.client.dictionary.CategorySystemNamesTable;
import org.openelis.modules.utilities.client.dictionary.DictionaryScreen;
import org.openelis.modules.utilities.client.dictionary.DictionaryEntriesTable;
import org.openelis.modules.utilities.client.organizeFavorites.OrganizeFavorites;
import org.openelis.modules.utilities.client.standardNote.StandardNoteNameTable;
import org.openelis.modules.utilities.client.standardNote.StandardNoteScreen;

public class Utilities implements AppModule {

    public void onModuleLoad() {
        ScreenBase.getWidgetMap().addWidget("StandardNoteNameTable", new StandardNoteNameTable());
        ScreenBase.getWidgetMap().addWidget("CategorySystemNamesTable", new CategorySystemNamesTable());
        ScreenBase.getWidgetMap().addWidget("DictionaryEntriesTable", new DictionaryEntriesTable());
        ScreenBase.getWidgetMap().addWidget("UtilitiesModule", this);
    }

    public String getModuleName() {
        // TODO Auto-generated method stub
        return "Utilities";
    }

    public void onClick(Widget sender) {
    	String key = ((ScreenWidget)sender).key;
        if(key.equals("dictionaryRow") || key.equals("favLeftDictionaryRow")) 
            OpenELIS.browser.addScreen(new DictionaryScreen(), "Dictionary", "Dictionary", "Loading");
        
        if(key.equals("standardNoteRow") || key.equals("favLeftStandardNoteRow"))
            OpenELIS.browser.addScreen(new StandardNoteScreen(), "Standard Note", "Standard Note","Loading");
        
        if(key.equals("organizeFavoritesLeft") || key.equals("organizeFavoritesLabel") || key.equals("organizeFavoritesLeft"))
            OpenELIS.browser.addScreen(new OrganizeFavorites(), "Organize Favorites", "Organize Favorites","Loading");
    }
}
