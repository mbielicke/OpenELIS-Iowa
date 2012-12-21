package org.openelis.modules.section.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.SectionDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.manager.SectionManager;
import org.openelis.manager.SectionParameterManager;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SectionServiceIntAsync {

    void abortUpdate(Integer id, AsyncCallback<SectionManager> callback);

    void add(SectionManager man, AsyncCallback<SectionManager> callback);

    void fetchById(Integer id, AsyncCallback<SectionManager> callback);

    void fetchByName(String search, AsyncCallback<ArrayList<SectionDO>> callback);

    void fetchForUpdate(Integer id, AsyncCallback<SectionManager> callback);

    void fetchParameterBySectionId(Integer id, AsyncCallback<SectionParameterManager> callback);

    void fetchWithParameters(Integer id, AsyncCallback<SectionManager> callback);

    void query(Query query, AsyncCallback<ArrayList<IdNameVO>> callback);

    void update(SectionManager man, AsyncCallback<SectionManager> callback);

}
