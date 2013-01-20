package org.openelis.modules.standardnote.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.StandardNoteDO;
import org.openelis.gwt.common.data.Query;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("standardNote")
public interface StandardNoteServiceInt extends RemoteService {

    StandardNoteDO fetchById(Integer id) throws Exception;

    ArrayList<StandardNoteDO> fetchByNameOrDescription(Query query) throws Exception;

    StandardNoteDO fetchBySystemVariableName(String name) throws Exception;

    ArrayList<StandardNoteDO> fetchByType(Integer typeId) throws Exception;

    ArrayList<IdNameVO> query(Query query) throws Exception;

    StandardNoteDO add(StandardNoteDO data) throws Exception;

    StandardNoteDO update(StandardNoteDO data) throws Exception;

    StandardNoteDO fetchForUpdate(Integer id) throws Exception;

    void delete(StandardNoteDO data) throws Exception;

    StandardNoteDO abortUpdate(Integer id) throws Exception;

}