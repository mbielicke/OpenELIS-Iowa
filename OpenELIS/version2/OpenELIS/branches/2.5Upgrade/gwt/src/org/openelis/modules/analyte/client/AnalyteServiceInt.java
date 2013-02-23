package org.openelis.modules.analyte.client;

import java.util.ArrayList;

import org.openelis.domain.AnalyteDO;
import org.openelis.domain.AnalyteViewDO;
import org.openelis.domain.IdNameVO;
import org.openelis.gwt.common.data.Query;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("analyte")
public interface AnalyteServiceInt extends RemoteService {

    AnalyteViewDO fetchById(Integer id) throws Exception;

    ArrayList<IdNameVO> query(Query query) throws Exception;

    AnalyteDO add(AnalyteViewDO data) throws Exception;

    AnalyteDO update(AnalyteViewDO data) throws Exception;

    AnalyteViewDO fetchForUpdate(Integer id) throws Exception;

    void delete(AnalyteDO data) throws Exception;

    AnalyteViewDO abortUpdate(Integer id) throws Exception;

    ArrayList<AnalyteDO> fetchByName(String search) throws Exception;

}