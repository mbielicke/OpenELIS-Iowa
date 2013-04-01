package org.openelis.modules.worksheetBuilder.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.WorksheetBuilderVO;
import org.openelis.ui.common.data.Query;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("worksheetBuilder")
public interface WorksheetBuilderServiceInt extends RemoteService {

    ArrayList<IdNameVO> query(Query query) throws Exception;

    ArrayList<WorksheetBuilderVO> lookupAnalyses(Query query) throws Exception;

    ArrayList<IdNameVO> getColumnNames(Integer formatId) throws Exception;

}