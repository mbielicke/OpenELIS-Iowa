package org.openelis.modules.analysis.client;

import com.google.gwt.user.client.ui.Widget;

import org.openelis.gwt.screen.AppModule;
import org.openelis.gwt.screen.ScreenBase;
import org.openelis.gwt.screen.ScreenWidget;
import org.openelis.modules.analysis.client.qaevent.QAEventScreen;
import org.openelis.modules.analysis.client.qaevent.QAEventsNamesTable;
import org.openelis.modules.main.client.openelis.OpenELIS;

public class Analysis implements AppModule {

    
    public void onModuleLoad() {
        ScreenBase.getWidgetMap().addWidget("QAEventsNamesTable", new QAEventsNamesTable()); 
        ScreenBase.getWidgetMap().addWidget("AnalysisModule", this);
    }

    public void execute(String id) {
        // TODO Auto-generated method stub
        
    }

    public String getModuleName() {
        return "Analysis";
    }

    
   /* public void onClick(Widget sender) {
    	String key = ((ScreenWidget)sender).key;
        if(key.equals("qaEventsIcon") || key.equals("qaEventsLabel") || key.equals("qaEventsDescription") || 
        		key.equals("favLeftqaEvents")) 
            OpenELIS.browser.addScreen(new QAEvent(), "QA Events", "QAEvents", "Loading");
        
    }*/
    
    public void onMouseUp(Widget sender, int x, int y) {
    	String key = ((ScreenWidget)sender).key;
        if(key.equals("qaEventsRow") || key.equals("favQaEventsRow") || key.equals("favLeftQaEventsRow")) 
            OpenELIS.browser.addScreen(new QAEventScreen(), "QA Events", "QAEvents", "Loading");
    	
    }

	public void onClick(Widget sender) {
		// TODO Auto-generated method stub
		
	}

	public void onMouseDown(Widget sender, int x, int y) {
		// TODO Auto-generated method stub
		
	}

	public void onMouseEnter(Widget sender) {
		// TODO Auto-generated method stub
		
	}

	public void onMouseLeave(Widget sender) {
		// TODO Auto-generated method stub
		
	}

	public void onMouseMove(Widget sender, int x, int y) {
		// TODO Auto-generated method stub
		
	}

}
