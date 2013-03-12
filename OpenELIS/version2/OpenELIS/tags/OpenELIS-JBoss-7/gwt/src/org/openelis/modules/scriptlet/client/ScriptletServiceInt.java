package org.openelis.modules.scriptlet.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("scriptlet")
public interface ScriptletServiceInt extends RemoteService {

    ArrayList<IdNameVO> fetchByName(String search) throws Exception;

}