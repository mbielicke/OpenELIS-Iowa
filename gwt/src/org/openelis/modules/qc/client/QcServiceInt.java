package org.openelis.modules.qc.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.QcDO;
import org.openelis.domain.QcLotDO;
import org.openelis.domain.QcLotViewDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.manager.QcAnalyteManager;
import org.openelis.manager.QcLotManager;
import org.openelis.manager.QcManager;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("qc")
public interface QcServiceInt extends RemoteService {

    QcManager fetchById(Integer id) throws Exception;

    ArrayList<QcDO> fetchByName(String search) throws Exception;

    ArrayList<QcLotViewDO> fetchActiveByName(Query query) throws Exception;

    ArrayList<QcLotViewDO> fetchActiveByName(String search) throws Exception;

    QcManager fetchWithAnalytes(Integer id) throws Exception;

    QcManager fetchWithLots(Integer id) throws Exception;

    QcLotViewDO fetchLotById(Integer id) throws Exception;

    ArrayList<IdNameVO> query(Query query) throws Exception;

    QcManager add(QcManager man) throws Exception;

    QcManager update(QcManager man) throws Exception;

    QcManager fetchForUpdate(Integer id) throws Exception;

    QcManager abortUpdate(Integer id) throws Exception;

    QcManager duplicate(Integer id) throws Exception;

    //
    // support for QcAnalyteManager and QcLotManager
    //
    QcAnalyteManager fetchAnalyteByQcId(Integer id) throws Exception;

    QcLotManager fetchLotByQcId(Integer id) throws Exception;

    QcLotDO validateForDelete(QcLotViewDO data) throws Exception;

}