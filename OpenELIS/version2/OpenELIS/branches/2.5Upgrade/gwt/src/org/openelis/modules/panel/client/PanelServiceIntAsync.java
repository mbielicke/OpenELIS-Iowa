package org.openelis.modules.panel.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.IdVO;
import org.openelis.domain.TestMethodVO;
import org.openelis.gwt.common.data.Query;
import org.openelis.manager.PanelItemManager;
import org.openelis.manager.PanelManager;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface PanelServiceIntAsync {

    void abortUpdate(Integer id, AsyncCallback<PanelManager> callback);

    void add(PanelManager man, AsyncCallback<PanelManager> callback);

    void delete(PanelManager man, AsyncCallback<Void> callback);

    void fetchAuxIdsByPanelId(Integer panelId, AsyncCallback<ArrayList<IdVO>> callback);

    void fetchById(Integer id, AsyncCallback<PanelManager> callback);

    void fetchByNameSampleTypeWithTests(Query query, AsyncCallback<ArrayList<TestMethodVO>> callback);

    void fetchByNameWithTests(String name, AsyncCallback<ArrayList<TestMethodVO>> callback);

    void fetchForUpdate(Integer id, AsyncCallback<PanelManager> callback);

    void fetchItemByPanelId(Integer id, AsyncCallback<PanelItemManager> callback);

    void fetchTestIdsByPanelId(Integer panelId, AsyncCallback<ArrayList<IdVO>> callback);

    void fetchWithItems(Integer id, AsyncCallback<PanelManager> callback);

    void query(Query query, AsyncCallback<ArrayList<IdNameVO>> callback);

    void update(PanelManager man, AsyncCallback<PanelManager> callback);

}
