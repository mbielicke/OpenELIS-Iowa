package org.openelis.modules.analyte1.client;

import java.util.ArrayList;

import org.openelis.domain.AnalyteDO;
import org.openelis.domain.AnalyteViewDO;
import org.openelis.domain.IdNameVO;
import org.openelis.ui.annotation.Service;
import org.openelis.ui.common.data.Query;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@Service
@RemoteServiceRelativePath("analyte1")
public interface AnalyteService1 extends XsrfProtectedService {

    AnalyteViewDO fetchById(Integer id) throws Exception;

    ArrayList<IdNameVO> query(Query query) throws Exception;

    AnalyteDO add(AnalyteViewDO data) throws Exception;

    AnalyteDO update(AnalyteViewDO data) throws Exception;

    AnalyteViewDO fetchForUpdate(Integer id) throws Exception;

    void delete(AnalyteDO data) throws Exception;

    AnalyteViewDO abortUpdate(Integer id) throws Exception;

    ArrayList<AnalyteDO> fetchByName(String search) throws Exception;

}