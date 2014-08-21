package org.openelis.modules.dictionary.client;

import org.openelis.gwt.screen.AppModule;
import org.openelis.gwt.screen.ClassFactory;

public class DictionaryEntry implements AppModule {

    public void onModuleLoad() {
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
    }

    public String getModuleName() {
        return "Dictionary";
    }


}
