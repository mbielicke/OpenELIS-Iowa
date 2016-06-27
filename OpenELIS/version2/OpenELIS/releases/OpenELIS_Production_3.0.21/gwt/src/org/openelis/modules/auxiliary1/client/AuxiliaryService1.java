package org.openelis.modules.auxiliary1.client;

import java.util.ArrayList;

import org.openelis.domain.AuxFieldGroupDO;
import org.openelis.domain.AuxFieldViewDO;
import org.openelis.domain.IdNameVO;
import org.openelis.manager.AuxFieldGroupManager1;
import org.openelis.ui.annotation.Service;
import org.openelis.ui.common.data.Query;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@Service
@RemoteServiceRelativePath("auxiliary1")
public interface AuxiliaryService1 extends XsrfProtectedService {

    ArrayList<AuxFieldGroupDO> fetchActive() throws Exception;

    ArrayList<AuxFieldViewDO> fetchAll() throws Exception;

    ArrayList<AuxFieldViewDO> fetchByAnalyteName(String search) throws Exception;

    // manager methods
    AuxFieldGroupManager1 fetchById(Integer id) throws Exception;

    ArrayList<AuxFieldGroupManager1> fetchByIds(ArrayList<Integer> ids) throws Exception;

    ArrayList<IdNameVO> query(Query query) throws Exception;

    AuxFieldGroupManager1 add(AuxFieldGroupManager1 man) throws Exception;

    AuxFieldGroupManager1 update(AuxFieldGroupManager1 man) throws Exception;

    AuxFieldGroupManager1 fetchForUpdate(Integer id) throws Exception;

    AuxFieldGroupManager1 abortUpdate(Integer id) throws Exception;
    
    AuxFieldViewDO validateForDelete(AuxFieldViewDO data) throws Exception;
}