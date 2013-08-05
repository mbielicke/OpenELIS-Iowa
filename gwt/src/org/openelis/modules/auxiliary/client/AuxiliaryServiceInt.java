package org.openelis.modules.auxiliary.client;

import java.util.ArrayList;

import org.openelis.domain.AuxFieldGroupDO;
import org.openelis.domain.IdNameVO;
import org.openelis.ui.common.data.Query;
import org.openelis.manager.AuxFieldGroupManager;
import org.openelis.manager.AuxFieldManager;
import org.openelis.manager.AuxFieldValueManager;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("auxiliary")
public interface AuxiliaryServiceInt extends XsrfProtectedService {

    ArrayList<AuxFieldGroupDO> fetchActive() throws Exception;

    // manager methods
    AuxFieldGroupManager fetchById(Integer id) throws Exception;
    
    ArrayList<AuxFieldGroupManager> fetchByIds(ArrayList<Integer> ids) throws Exception;

    AuxFieldGroupManager fetchGroupByIdWithFields(Integer id) throws Exception;

    ArrayList<IdNameVO> query(Query query) throws Exception;

    AuxFieldGroupManager add(AuxFieldGroupManager man) throws Exception;

    AuxFieldGroupManager update(AuxFieldGroupManager man) throws Exception;

    AuxFieldGroupManager fetchForUpdate(Integer id) throws Exception;

    AuxFieldGroupManager abortUpdate(Integer id) throws Exception;

    // other managers
    AuxFieldManager fetchFieldById(Integer id) throws Exception;

    AuxFieldManager fetchFieldByGroupId(Integer groupId) throws Exception;

    AuxFieldManager fetchFieldByGroupIdWithValues(Integer groupId) throws Exception;

    AuxFieldValueManager fetchFieldValueByFieldId(Integer fieldId) throws Exception;

}