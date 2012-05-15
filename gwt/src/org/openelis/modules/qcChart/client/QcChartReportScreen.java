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
package org.openelis.modules.qcChart.client;

import org.openelis.gwt.screen.ScreenDef;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.ScreenWindowInt;
import org.openelis.modules.report.client.ReportScreen;

/**
 * This class is used to execute reports on behalf of those screens that don't
 * implement ReportScreen like QcChartScreen
 */
public class QcChartReportScreen extends ReportScreen {

    public QcChartReportScreen(ScreenWindowInt window) throws Exception {
        drawScreen(new ScreenDef());
        setName(consts.get("qcChartReport"));
        this.window = window;
        service = new ScreenService("controller?service=org.openelis.modules.qcChart.server.QcChartService");
    }

    /**
     * This method is overridden in order to make sure that the super class's
     * method with the same name doesn't get called, because the screens using
     * this class won't get prompts
     */
    protected void getReportParameters() {
    }
}
