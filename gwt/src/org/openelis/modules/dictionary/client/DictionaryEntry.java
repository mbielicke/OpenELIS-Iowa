package org.openelis.modules.dictionary.client;

import com.google.gwt.user.client.ui.Widget;

import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.screen.AppModule;
import org.openelis.gwt.screen.ClassFactory;
import org.openelis.modules.main.client.openelis.OpenELIS;

public class DictionaryEntry implements AppModule {

    public void onModuleLoad() {
    	OpenELIS.modules.addItem(new StringObject(getModuleName()));
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

    public void onClick(Widget sender) {
        // TODO Auto-generated method stub
        
    }


}
