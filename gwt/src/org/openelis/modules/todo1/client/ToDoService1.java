package org.openelis.modules.todo1.client;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;
import java.util.ArrayList;
import org.openelis.domain.AnalysisViewVO;
import org.openelis.domain.ToDoSampleViewVO;
import org.openelis.domain.ToDoWorksheetVO;
import org.openelis.ui.annotation.Service;
import org.openelis.ui.common.ReportStatus;

@Service
@RemoteServiceRelativePath("todo1")
public abstract interface ToDoService1 extends XsrfProtectedService {
    public abstract ArrayList<AnalysisViewVO> getLoggedIn() throws Exception;

    public abstract ArrayList<AnalysisViewVO> getInitiated() throws Exception;

    public abstract ArrayList<AnalysisViewVO> getCompleted() throws Exception;

    public abstract ArrayList<AnalysisViewVO> getReleased() throws Exception;

    public abstract ArrayList<ToDoSampleViewVO> getToBeVerified() throws Exception;

    public abstract ArrayList<AnalysisViewVO> getOther() throws Exception;

    public abstract ArrayList<ToDoWorksheetVO> getWorksheet() throws Exception;

    public abstract ReportStatus exportToExcel(boolean paramBoolean) throws Exception;

    public abstract ReportStatus getExportToExcelStatus() throws Exception;
}