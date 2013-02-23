package org.openelis.modules.worksheetCompletion.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.gwt.common.ReportStatus;
import org.openelis.manager.WorksheetManager;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("worksheetCompletion")
public interface WorksheetCompletionServiceInt extends RemoteService {

    WorksheetManager saveForEdit(WorksheetManager manager) throws Exception;

    WorksheetManager loadFromEdit(WorksheetManager manager) throws Exception;

    ArrayList<IdNameVO> getHeaderLabelsForScreen(WorksheetManager manager) throws Exception;

    ReportStatus getUpdateStatus();

}