package org.openelis.modules.worksheetCreation.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.WorksheetCreationVO;
import org.openelis.ui.common.data.Query;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("worksheetCreation")
public interface WorksheetCreationServiceInt extends XsrfProtectedService {

    ArrayList<WorksheetCreationVO> query(Query query) throws Exception;

    ArrayList<IdNameVO> getColumnNames(Integer formatId) throws Exception;

}