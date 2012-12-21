package org.openelis.modules.method.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.MethodDO;
import org.openelis.gwt.common.data.Query;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("method")
public interface MethodServiceInt extends RemoteService {

    ArrayList<MethodDO> fetchByName(String search) throws Exception;

    MethodDO fetchById(Integer id) throws Exception;

    ArrayList<IdNameVO> query(Query query) throws Exception;

    MethodDO add(MethodDO data) throws Exception;

    MethodDO update(MethodDO data) throws Exception;

    MethodDO fetchForUpdate(Integer id) throws Exception;

    MethodDO abortUpdate(Integer id) throws Exception;

}