package org.openelis.modules.analysis.client;

import com.google.gwt.user.client.ui.Widget;

import org.openelis.gwt.screen.AppModule;
import org.openelis.gwt.screen.ClassFactory;
import org.openelis.gwt.screen.ScreenWidget;
import org.openelis.modules.analysis.client.qaevent.QAEventScreen;
import org.openelis.modules.analysis.client.qaevent.QAEventsNamesTable;
import org.openelis.modules.main.client.openelis.OpenELIS;

public class Analysis implements AppModule {

    
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
                                       return new org.openelis.modules.analysis.client.qaevent.QAEventScreen();
                                   }
                                }
        );
    }

    public void execute(String id) {
        // TODO Auto-generated method stub
        
    }

    public String getModuleName() {
        return "Analysis";
    }

	public void onClick(Widget sender) {
		String key = ((ScreenWidget)sender).key;
        if(key.equals("QAEventRow") || key.equals("favLeftQaEventsRow")) 
            OpenELIS.browser.addScreen(new QAEventScreen(), "QA Events", "QAEvents", "Loading");		
	}
}
