package org.openelis.modules.standardnote.client;

import org.openelis.gwt.screen.AppModule;
import org.openelis.gwt.screen.ClassFactory;

public class StandardNoteEntry implements AppModule {

    public void onModuleLoad() {
        ClassFactory.addClass(new String[] {"StandardNoteNameTable"}, 
                              new ClassFactory.Factory() {
                                  public Object newInstance(Object[] args) {
                                      return new StandardNoteNameTable();
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
        return "StandardNote";
    }

}
