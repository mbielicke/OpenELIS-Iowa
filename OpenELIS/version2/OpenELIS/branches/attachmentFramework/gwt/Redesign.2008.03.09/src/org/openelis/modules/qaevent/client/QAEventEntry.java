package org.openelis.modules.qaevent.client;

import org.openelis.gwt.screen.AppModule;
import org.openelis.gwt.screen.ClassFactory;

public class QAEventEntry implements AppModule {

    
    public void onModuleLoad() {
        ClassFactory.addClass(new String[] {"QAEventsNamesTable"},
                              new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args){
                                      return new QAEventsNamesTable();
                                  }
                              }
        );
        ClassFactory.addClass(new String[] {"QAEventScreen"},
                               new ClassFactory.Factory() {
                                   public Object newInstance(Object[] args) {
                                       return new org.openelis.modules.qaevent.client.QAEventScreen();
                                   }
                                }
        );
    }

    public String getModuleName() {
        return "Analysis";
    }

}
