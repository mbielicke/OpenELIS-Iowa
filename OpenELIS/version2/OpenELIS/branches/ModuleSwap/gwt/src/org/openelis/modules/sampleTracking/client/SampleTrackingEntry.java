package org.openelis.modules.sampleTracking.client;

import static org.openelis.modules.main.client.Logger.remote;

import java.util.logging.Level;

import org.openelis.modules.main.client.OpenELIS;
import org.openelis.modules.main.client.OpenELISEntry;
import org.openelis.modules.main.client.event.ShowScreenHandler;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.Window;


public class SampleTrackingEntry implements EntryPoint {

    @Override
    public void onModuleLoad() {
       OpenELISEntry.mainBus.addHandler(OpenELIS.SAMPLE_TRACKING, new ShowScreenHandler() {
           public void showScreen() {
               GWT.runAsync(new RunAsyncCallback() {
                   public void onSuccess() {
                       try {
                           org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                           window.setName(OpenELIS.getMessages().tracking());
                           window.setSize("20px", "20px");
                           window.setContent(new SampleTrackingScreen(window));
                           OpenELIS.getBrowser().addWindow(window, "tracking");
                       } catch (Throwable e) {
                           remote().log(Level.SEVERE, e.getMessage(), e);
                           Window.alert(e.getMessage());
                       }
                   }

                   public void onFailure(Throwable caught) {
                       remote().log(Level.SEVERE, caught.getMessage(), caught);
                       Window.alert(caught.getMessage());
                   }
               });
           }
       });
        
    }
    
    

}
