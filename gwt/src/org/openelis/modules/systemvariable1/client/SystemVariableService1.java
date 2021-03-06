package org.openelis.modules.systemvariable1.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.SystemVariableDO;
import org.openelis.ui.annotation.Service;
import org.openelis.ui.common.data.Query;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@Service
@RemoteServiceRelativePath("systemvariable1")
public interface SystemVariableService1 extends XsrfProtectedService {
    
    SystemVariableDO fetchById(Integer id) throws Exception;

    ArrayList<SystemVariableDO> fetchByName(String name) throws Exception;

    SystemVariableDO fetchByExactName(String name) throws Exception;

    ArrayList<IdNameVO> query(Query query) throws Exception;

    SystemVariableDO add(SystemVariableDO data) throws Exception;

    SystemVariableDO update(SystemVariableDO data) throws Exception;

    SystemVariableDO fetchForUpdate(Integer id) throws Exception;

    void delete(SystemVariableDO data) throws Exception;

    SystemVariableDO unlock(Integer id) throws Exception;
    
}
