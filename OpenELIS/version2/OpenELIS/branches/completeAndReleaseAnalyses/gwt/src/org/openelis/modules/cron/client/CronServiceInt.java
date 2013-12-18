package org.openelis.modules.cron.client;

import java.util.ArrayList;

import org.openelis.domain.CronDO;
import org.openelis.domain.IdNameVO;
import org.openelis.ui.common.data.Query;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("cron")
public interface CronServiceInt extends XsrfProtectedService {

    CronDO fetchById(Integer id) throws Exception;

    ArrayList<IdNameVO> query(Query query) throws Exception;

    CronDO add(CronDO data) throws Exception;

    CronDO update(CronDO data) throws Exception;

    CronDO fetchForUpdate(Integer id) throws Exception;

    void delete(CronDO data) throws Exception;

    CronDO abortUpdate(Integer id) throws Exception;

}