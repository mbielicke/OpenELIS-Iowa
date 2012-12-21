package org.openelis.modules.analyteParameter.client;

import java.util.ArrayList;

import org.openelis.domain.AnalyteParameterViewDO;
import org.openelis.domain.ReferenceIdTableIdNameVO;
import org.openelis.gwt.common.data.Query;
import org.openelis.manager.AnalyteParameterManager;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("analyteParameter")
public interface AnalyteParameterServiceInt extends RemoteService {

    AnalyteParameterManager fetchActiveByReferenceIdReferenceTableId(Query query) throws Exception;

    ArrayList<AnalyteParameterViewDO> fetchByAnalyteIdReferenceIdReferenceTableId(Query query) throws Exception;

    ArrayList<ReferenceIdTableIdNameVO> query(Query query) throws Exception;

    AnalyteParameterManager add(AnalyteParameterManager man) throws Exception;

    AnalyteParameterManager update(AnalyteParameterManager man) throws Exception;

    AnalyteParameterManager fetchForUpdate(AnalyteParameterManager man) throws Exception;

    AnalyteParameterManager abortUpdate(AnalyteParameterManager man) throws Exception;

}