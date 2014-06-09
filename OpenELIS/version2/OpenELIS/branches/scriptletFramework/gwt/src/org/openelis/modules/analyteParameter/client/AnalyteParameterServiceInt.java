package org.openelis.modules.analyteParameter.client;

import java.util.ArrayList;

import org.openelis.domain.AnalyteParameterViewDO;
import org.openelis.domain.ReferenceIdTableIdNameVO;
import org.openelis.manager.AnalyteParameterManager;
import org.openelis.ui.common.data.Query;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("analyteParameter")
public interface AnalyteParameterServiceInt extends XsrfProtectedService {

    AnalyteParameterManager fetchActiveByReferenceIdReferenceTableId(Query query) throws Exception;

    ArrayList<AnalyteParameterViewDO> fetchByAnalyteIdReferenceIdReferenceTableId(Query query) throws Exception;
    
    ArrayList<AnalyteParameterViewDO> fetchByReferenceIdsReferenceTableId(ArrayList<Integer> refIds,
                                                                                 Integer refTableId) throws Exception;
    ArrayList<ReferenceIdTableIdNameVO> query(Query query) throws Exception;

    AnalyteParameterManager add(AnalyteParameterManager man) throws Exception;

    AnalyteParameterManager update(AnalyteParameterManager man) throws Exception;

    AnalyteParameterManager fetchForUpdate(AnalyteParameterManager man) throws Exception;

    AnalyteParameterManager abortUpdate(AnalyteParameterManager man) throws Exception;

}