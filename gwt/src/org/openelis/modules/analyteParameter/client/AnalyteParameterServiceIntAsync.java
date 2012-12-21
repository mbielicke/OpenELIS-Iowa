package org.openelis.modules.analyteParameter.client;

import java.util.ArrayList;

import org.openelis.domain.AnalyteParameterViewDO;
import org.openelis.domain.ReferenceIdTableIdNameVO;
import org.openelis.gwt.common.data.Query;
import org.openelis.manager.AnalyteParameterManager;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AnalyteParameterServiceIntAsync {

    void abortUpdate(AnalyteParameterManager man, AsyncCallback<AnalyteParameterManager> callback);

    void add(AnalyteParameterManager man, AsyncCallback<AnalyteParameterManager> callback);

    void fetchActiveByReferenceIdReferenceTableId(Query query,
                                                  AsyncCallback<AnalyteParameterManager> callback);

    void fetchByAnalyteIdReferenceIdReferenceTableId(Query query,
                                                     AsyncCallback<ArrayList<AnalyteParameterViewDO>> callback);

    void fetchForUpdate(AnalyteParameterManager man, AsyncCallback<AnalyteParameterManager> callback);

    void query(Query query, AsyncCallback<ArrayList<ReferenceIdTableIdNameVO>> callback);

    void update(AnalyteParameterManager man, AsyncCallback<AnalyteParameterManager> callback);

}
