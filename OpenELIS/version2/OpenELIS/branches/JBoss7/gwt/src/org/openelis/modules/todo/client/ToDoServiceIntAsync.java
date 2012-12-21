package org.openelis.modules.todo.client;

import java.util.ArrayList;

import org.openelis.domain.AnalysisViewVO;
import org.openelis.domain.ToDoSampleViewVO;
import org.openelis.domain.ToDoWorksheetVO;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ToDoServiceIntAsync {

    void getCompleted(AsyncCallback<ArrayList<AnalysisViewVO>> callback);

    void getInitiated(AsyncCallback<ArrayList<AnalysisViewVO>> callback);

    void getLoggedIn(AsyncCallback<ArrayList<AnalysisViewVO>> callback);

    void getOther(AsyncCallback<ArrayList<AnalysisViewVO>> callback);

    void getReleased(AsyncCallback<ArrayList<AnalysisViewVO>> callback);

    void getToBeVerified(AsyncCallback<ArrayList<ToDoSampleViewVO>> callback);

    void getWorksheet(AsyncCallback<ArrayList<ToDoWorksheetVO>> callback);

}
