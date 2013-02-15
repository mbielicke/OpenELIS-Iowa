package org.openelis.modules.systemvariable.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.SystemVariableDO;
import org.openelis.gwt.common.data.Query;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("systemVariable")
public interface SystemVariableServiceInt extends RemoteService {

    SystemVariableDO fetchById(Integer id) throws Exception;

    ArrayList<SystemVariableDO> fetchByName(String name) throws Exception;

    ArrayList<IdNameVO> query(Query query) throws Exception;

    SystemVariableDO add(SystemVariableDO data) throws Exception;

    SystemVariableDO update(SystemVariableDO data) throws Exception;

    SystemVariableDO fetchForUpdate(Integer id) throws Exception;

    void delete(SystemVariableDO data) throws Exception;

    SystemVariableDO abortUpdate(Integer id) throws Exception;

}