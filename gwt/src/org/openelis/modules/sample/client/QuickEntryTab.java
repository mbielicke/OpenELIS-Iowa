package org.openelis.modules.sample.client;

import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.widget.ScreenWindowInt;
import org.openelis.manager.SampleManager;
import org.openelis.ui.widget.WindowInt;

/**
 * This class is used to act a placeholder on Sample Tracking screen for the samples
 * belonging to the domain Quick Entry 
 */
public class QuickEntryTab extends Screen {

    public QuickEntryTab(WindowInt window) {
        this(null, window); 
    }
    
    public QuickEntryTab(ScreenDefInt def, WindowInt window) {
        setDefinition(def);
        setWindow(window);
        initialize();
    }

    private void initialize() {
    }

    public void setData(SampleManager manager) {
    }

    public void draw() {
    }
}