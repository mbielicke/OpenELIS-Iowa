package org.openelis.modules.worksheet1.client;

import java.util.ArrayList;

import org.openelis.domain.IdVO;
import org.openelis.manager.WorksheetManager1;
import org.openelis.ui.common.data.Query;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("worksheet1")
public interface WorksheetServiceInt1 extends RemoteService {
    public WorksheetManager1 getInstance() throws Exception;
    public WorksheetManager1 fetchById(Integer worksheetId, WorksheetManager1.Load... elements) throws Exception;
    public ArrayList<IdVO> fetchByQuery(Query query) throws Exception;
    public WorksheetManager1 fetchForUpdate(Integer worksheetId) throws Exception;
}