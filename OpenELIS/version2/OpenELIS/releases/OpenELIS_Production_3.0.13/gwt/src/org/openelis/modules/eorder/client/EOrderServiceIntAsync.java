package org.openelis.modules.eorder.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;

import org.openelis.domain.EOrderDO;

public interface EOrderServiceIntAsync {

    void fetchById(Integer id, AsyncCallback<EOrderDO> callback);

    void fetchByPaperOrderValidator(String pov, AsyncCallback<ArrayList<EOrderDO>> callback);

}
