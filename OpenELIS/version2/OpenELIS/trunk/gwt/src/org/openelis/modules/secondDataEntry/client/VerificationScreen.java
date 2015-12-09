/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.modules.secondDataEntry.client;

import org.openelis.cache.CacheProvider;
import org.openelis.manager.SampleManager1;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.widget.WindowInt;

import com.google.gwt.user.client.ui.Widget;

/**
 * This class defines basic functionality for a screen/tab that shows the fields
 * for second data entry and verification
 */
public abstract class VerificationScreen extends Screen {
    protected Widget         tabFocusLostWidget;
    protected SampleManager1 manager;

    /**
     * Returns that widget lost focus when Tab was pressed most recently
     */
    public Widget getTabFocusLostWidget() {
        return tabFocusLostWidget;
    }

    /**
     * Sets the passed widget as the one that lost focus when Tab was pressed
     * most recently
     */
    public void setTabFocusLostWidget(Widget tabFocusLostWidget) {
        this.tabFocusLostWidget = tabFocusLostWidget;
    }

    /**
     * Returns the manager whose data is being verified currently on the
     * screen/tab
     */
    public abstract SampleManager1 getManager();

    /**
     * Returns the window for the screen/tab
     */
    public abstract WindowInt getWindow();

    /**
     * Returns the CacheProvider that provides quick access to some other data
     * used for verification e.g. AuxFieldGroupManagers
     */
    public abstract CacheProvider getCacheProvider();

    /**
     * Returns the Validation object currently being used for validating the
     * widgets on the screen/tab
     */
    public abstract Validation getValidation();
}