package org.openelis.modules.worksheetCreation.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.WorksheetCreationVO;
import org.openelis.gwt.common.data.Query;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("worksheetCreation")
public interface WorksheetCreationServiceInt extends RemoteService {

    ArrayList<WorksheetCreationVO> query(Query query) throws Exception;

    ArrayList<IdNameVO> getColumnNames(Integer formatId) throws Exception;

}