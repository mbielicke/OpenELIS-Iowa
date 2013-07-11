package org.openelis.modules.worksheetCompletion.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.manager.WorksheetManager;
import org.openelis.ui.common.ReportStatus;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("worksheetCompletion")
public interface WorksheetCompletionServiceInt extends XsrfProtectedService {

    WorksheetManager saveForEdit(WorksheetManager manager) throws Exception;

    WorksheetManager loadFromEdit(WorksheetManager manager) throws Exception;

    ArrayList<IdNameVO> getHeaderLabelsForScreen(WorksheetManager manager) throws Exception;

    ReportStatus getUpdateStatus();

}