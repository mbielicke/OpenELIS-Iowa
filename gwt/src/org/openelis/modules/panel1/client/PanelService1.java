package org.openelis.modules.panel1.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.IdVO;
import org.openelis.domain.PanelDO;
import org.openelis.domain.TestMethodVO;
import org.openelis.manager.PanelManager1;
import org.openelis.ui.annotation.Service;
import org.openelis.ui.common.data.Query;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@Service
@RemoteServiceRelativePath("panel1")
public interface PanelService1 extends XsrfProtectedService {

    PanelManager1 getInstance() throws Exception;

    PanelManager1 fetchById(Integer id) throws Exception;

    ArrayList<PanelDO> fetchByName(String name) throws Exception;

    ArrayList<TestMethodVO> fetchByNameWithTests(String name) throws Exception;

    ArrayList<TestMethodVO> fetchByNameSampleTypeWithTests(Query query) throws Exception;

    ArrayList<PanelDO> fetchAll() throws Exception;

    ArrayList<IdNameVO> query(Query query) throws Exception;

    PanelManager1 update(PanelManager1 man) throws Exception;

    PanelManager1 fetchForUpdate(Integer id) throws Exception;

    PanelManager1 unlock(Integer id) throws Exception;

    ArrayList<IdVO> fetchTestIdsByPanelId(Integer panelId) throws Exception;

    ArrayList<IdVO> fetchAuxIdsByPanelId(Integer panelId) throws Exception;

}