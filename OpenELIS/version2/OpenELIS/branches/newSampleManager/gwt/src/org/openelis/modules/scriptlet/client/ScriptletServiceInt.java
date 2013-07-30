package org.openelis.modules.scriptlet.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("scriptlet")
public interface ScriptletServiceInt extends XsrfProtectedService {

    ArrayList<IdNameVO> fetchByName(String search) throws Exception;

}