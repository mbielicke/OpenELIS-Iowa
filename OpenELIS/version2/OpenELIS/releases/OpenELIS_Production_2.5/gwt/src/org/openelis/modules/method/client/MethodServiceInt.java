package org.openelis.modules.method.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.MethodDO;
import org.openelis.ui.common.data.Query;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("method")
public interface MethodServiceInt extends XsrfProtectedService {

    ArrayList<MethodDO> fetchByName(String search) throws Exception;

    MethodDO fetchById(Integer id) throws Exception;

    ArrayList<IdNameVO> query(Query query) throws Exception;

    MethodDO add(MethodDO data) throws Exception;

    MethodDO update(MethodDO data) throws Exception;

    MethodDO fetchForUpdate(Integer id) throws Exception;

    MethodDO abortUpdate(Integer id) throws Exception;

}