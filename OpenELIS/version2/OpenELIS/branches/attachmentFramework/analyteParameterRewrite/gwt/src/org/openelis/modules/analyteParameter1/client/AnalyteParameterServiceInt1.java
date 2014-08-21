package org.openelis.modules.analyteParameter1.client;

import java.util.ArrayList;

import org.openelis.domain.ReferenceIdTableIdNameVO;
import org.openelis.manager.AnalyteParameterManager1;
import org.openelis.ui.common.data.Query;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("analyteParameter1")
public interface AnalyteParameterServiceInt1 extends XsrfProtectedService {
    public AnalyteParameterManager1 getInstance(Integer referenceId, Integer referenceTableId, String referenceName) throws Exception;
    
    public AnalyteParameterManager1 fetchByReferenceIdReferenceTableId(Integer referenceId, Integer referenceTableId) throws Exception;
    
    public ArrayList<ReferenceIdTableIdNameVO> query(Query query) throws Exception;
    
    public AnalyteParameterManager1 fetchForUpdate(Integer referenceId, Integer referenceTableId) throws Exception;
    
    public AnalyteParameterManager1 unlock(Integer referenceId, Integer referenceTableId) throws Exception;
}