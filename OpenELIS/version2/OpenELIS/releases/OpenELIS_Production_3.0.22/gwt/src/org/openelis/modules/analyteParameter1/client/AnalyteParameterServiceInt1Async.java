package org.openelis.modules.analyteParameter1.client;

import java.util.ArrayList;

import org.openelis.domain.ReferenceIdTableIdNameVO;
import org.openelis.manager.AnalyteParameterManager1;
import org.openelis.ui.common.data.Query;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AnalyteParameterServiceInt1Async {
    public void getInstance(Integer referenceId, Integer referenceTableId, AsyncCallback<AnalyteParameterManager1> callback);
    
    public void fetchByReferenceIdReferenceTableId(Integer referenceId, Integer referenceTableId,
                                            AsyncCallback<AnalyteParameterManager1> callback);
    
    public void query(Query query, AsyncCallback<ArrayList<ReferenceIdTableIdNameVO>> callback);

    public void fetchForUpdate(Integer referenceId, Integer referenceTableId,
                        AsyncCallback<AnalyteParameterManager1> callback);
    
    public void update(AnalyteParameterManager1 apm, AsyncCallback<AnalyteParameterManager1> callback);

    public void unlock(Integer referenceId, Integer referenceTableId,
                AsyncCallback<AnalyteParameterManager1> callback);
}
