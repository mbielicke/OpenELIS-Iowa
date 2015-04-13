package org.openelis.modules.eorder.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

import org.openelis.domain.EOrderDO;

@RemoteServiceRelativePath("eorder")
public interface EOrderServiceInt extends XsrfProtectedService {

    EOrderDO fetchById(Integer id) throws Exception;

    ArrayList<EOrderDO> fetchByPaperOrderValidator(String pov) throws Exception;

}