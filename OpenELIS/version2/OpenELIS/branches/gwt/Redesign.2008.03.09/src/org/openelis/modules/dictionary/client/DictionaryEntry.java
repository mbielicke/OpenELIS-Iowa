package org.openelis.modules.dictionary.client;

import com.google.gwt.user.client.ui.Widget;

import org.openelis.gwt.screen.AppModule;
import org.openelis.gwt.screen.ClassFactory;
import org.openelis.gwt.screen.ScreenBase;
import org.openelis.gwt.screen.ScreenWidget;
import org.openelis.modules.dictionary.client.CategorySystemNamesTable;
import org.openelis.modules.dictionary.client.DictionaryEntriesTable;
import org.openelis.modules.dictionary.client.DictionaryScreen;
import org.openelis.modules.favorites.client.FavoritesScreen;
import org.openelis.modules.main.client.openelis.OpenELIS;
import org.openelis.modules.standardnote.client.StandardNoteNameTable;
import org.openelis.modules.standardnote.client.StandardNoteScreen;

public class DictionaryEntry implements AppModule {

    public void onModuleLoad() {
        ClassFactory.addClass(new String[] {"StandardNoteNameTable"}, 
                              new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      return new StandardNoteNameTable();
                                  }
                              }
       );
        ClassFactory.addClass(new String[] {"CategorySystemNamesTable"}, 
                              new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      return new CategorySystemNamesTable();
                                  }
                              }
       );
        ClassFactory.addClass(new String[] {"DictionaryEntriesTable"}, 
                              new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      return new DictionaryEntriesTable();
                                  }
                              }
       );
        ClassFactory.addClass(new String[] {"DictionaryScreen"}, 
                               new ClassFactory.Factory() {
                                   public Object newInstance(Object[] args) {
                                       return new DictionaryScreen();
                                   }
                               }
        );
        ClassFactory.addClass(new String[] {"OrganizeFavorites"}, 
                               new ClassFactory.Factory() {
                                   public Object newInstance(Object[] args) {
                                       return new FavoritesScreen();
                                   }
                               }
        );
        ClassFactory.addClass(new String[] {"StandardNoteScreen"}, 
                               new ClassFactory.Factory() {
                                   public Object newInstance(Object[] args) {
                                       return new StandardNoteScreen();
                                   }
                               }
        );
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
            OpenELIS.browser.addScreen(new FavoritesScreen(), "Organize Favorites", "Organize Favorites","Loading");
    }
}
