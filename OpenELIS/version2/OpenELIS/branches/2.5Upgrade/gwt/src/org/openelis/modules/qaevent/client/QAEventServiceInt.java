package org.openelis.modules.qaevent.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.QaEventDO;
import org.openelis.domain.QaEventViewDO;
import org.openelis.gwt.common.data.Query;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("qa")
public interface QAEventServiceInt extends RemoteService {

    QaEventViewDO fetchById(Integer id) throws Exception;

    ArrayList<QaEventDO> fetchByTestId(Integer id) throws Exception;

    ArrayList<QaEventDO> fetchByCommon() throws Exception;

    ArrayList<IdNameVO> query(Query query) throws Exception;

    QaEventViewDO add(QaEventViewDO data) throws Exception;

    QaEventViewDO update(QaEventViewDO data) throws Exception;

    QaEventViewDO fetchForUpdate(Integer id) throws Exception;

    QaEventViewDO abortUpdate(Integer id) throws Exception;

}