/**
 * 
 */
package org.openelis.modules.report.client;

import java.util.ArrayList;

import org.openelis.gwt.screen.ScreenDef;
import org.openelis.modules.todo1.client.ToDoService1Impl;
import org.openelis.ui.common.Prompt;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.widget.WindowInt;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class ToDoReportScreen extends ReportScreen<Query> {

    private boolean mySection;
    
    private ToDoService1Impl service = ToDoService1Impl.INSTANCE;

    public ToDoReportScreen(WindowInt window) throws Exception {
        drawScreen(new ScreenDef());
        this.window = window;
    }

    /**
     * This method is overridden in order to make sure that the super class's
     * method with the same name doesn't get called, because the screens using
     * this class won't get prompts
     */
    protected void getReportParameters() {
    }

    @Override
    protected ArrayList<Prompt> getPrompts() throws Exception {
        return null;
    }

    public void setMySectionOnly(boolean mySection) {
        this.mySection = mySection;
    }

    @Override
    public void runReport(Query query, AsyncCallback<ReportStatus> callback) {
        service.exportToExcel(mySection, callback);
    }

}
