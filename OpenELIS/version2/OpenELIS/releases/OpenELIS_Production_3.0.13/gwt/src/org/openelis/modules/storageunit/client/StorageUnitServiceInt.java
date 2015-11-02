package org.openelis.modules.storageunit.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.StorageUnitDO;
import org.openelis.ui.common.data.Query;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("storageUnit")
public interface StorageUnitServiceInt extends XsrfProtectedService {

    StorageUnitDO fetchById(Integer id) throws Exception;

    ArrayList<IdNameVO> fetchByDescription(String search) throws Exception;

    ArrayList<IdNameVO> query(Query query) throws Exception;

    StorageUnitDO add(StorageUnitDO data) throws Exception;

    StorageUnitDO update(StorageUnitDO data) throws Exception;

    StorageUnitDO fetchForUpdate(Integer id) throws Exception;

    void delete(StorageUnitDO data) throws Exception;

    StorageUnitDO abortUpdate(Integer id) throws Exception;

}