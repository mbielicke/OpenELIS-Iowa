package org.openelis.modules.scriptlet.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.ScriptletDO;
import org.openelis.ui.common.data.Query;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("scriptlet")
public interface ScriptletServiceInt extends XsrfProtectedService {

    ArrayList<IdNameVO> fetchByName(String search) throws Exception;
    
    ArrayList<IdNameVO> query(Query query) throws Exception;
    
    ScriptletDO fetchById(Integer id) throws Exception;
    
    ArrayList<ScriptletDO> fetchByIds(ArrayList<Integer> ids) throws Exception;
    
    ScriptletDO fetchForUpdate(Integer id) throws Exception;
    
    ScriptletDO add(ScriptletDO data) throws Exception;
    
    ScriptletDO update(ScriptletDO data) throws Exception;
    
    ScriptletDO abortUpdate(Integer id) throws Exception;
}