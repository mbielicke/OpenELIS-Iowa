package org.openelis.modules.label.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.LabelDO;
import org.openelis.domain.LabelViewDO;
import org.openelis.gwt.common.data.Query;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("label")
public interface LabelServiceInt extends RemoteService {

    LabelViewDO fetchById(Integer id) throws Exception;

    ArrayList<LabelDO> fetchByName(String search) throws Exception;

    ArrayList<IdNameVO> query(Query query) throws Exception;

    LabelViewDO add(LabelViewDO data) throws Exception;

    LabelViewDO update(LabelViewDO data) throws Exception;

    LabelViewDO fetchForUpdate(Integer id) throws Exception;

    void delete(LabelViewDO data) throws Exception;

    LabelViewDO abortUpdate(Integer id) throws Exception;

}