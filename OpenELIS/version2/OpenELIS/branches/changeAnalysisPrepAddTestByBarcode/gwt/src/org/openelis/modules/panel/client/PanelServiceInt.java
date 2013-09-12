package org.openelis.modules.panel.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.IdVO;
import org.openelis.domain.PanelDO;
import org.openelis.domain.TestMethodVO;
import org.openelis.manager.PanelItemManager;
import org.openelis.manager.PanelManager;
import org.openelis.ui.common.data.Query;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("panel")
public interface PanelServiceInt extends XsrfProtectedService {

    PanelManager fetchById(Integer id) throws Exception;

    PanelManager fetchWithItems(Integer id) throws Exception;

    ArrayList<PanelDO> fetchByName(String name) throws Exception;

    ArrayList<TestMethodVO> fetchByNameWithTests(String name) throws Exception;

    ArrayList<TestMethodVO> fetchByNameSampleTypeWithTests(Query query) throws Exception;

    ArrayList<IdNameVO> query(Query query) throws Exception;

    PanelManager add(PanelManager man) throws Exception;

    PanelManager update(PanelManager man) throws Exception;

    void delete(PanelManager man) throws Exception;

    PanelManager fetchForUpdate(Integer id) throws Exception;

    PanelManager abortUpdate(Integer id) throws Exception;

    PanelItemManager fetchItemByPanelId(Integer id) throws Exception;

    ArrayList<IdVO> fetchTestIdsByPanelId(Integer panelId) throws Exception;

    ArrayList<IdVO> fetchAuxIdsByPanelId(Integer panelId) throws Exception;

}