package org.openelis.modules.worksheetCompletion.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.gwt.common.ReportStatus;
import org.openelis.manager.WorksheetManager;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface WorksheetCompletionServiceIntAsync {

    void getHeaderLabelsForScreen(WorksheetManager manager,
                                  AsyncCallback<ArrayList<IdNameVO>> callback);

    void getUpdateStatus(AsyncCallback<ReportStatus> callback);

    void loadFromEdit(WorksheetManager manager, AsyncCallback<WorksheetManager> callback);

    void saveForEdit(WorksheetManager manager, AsyncCallback<WorksheetManager> callback);

}
