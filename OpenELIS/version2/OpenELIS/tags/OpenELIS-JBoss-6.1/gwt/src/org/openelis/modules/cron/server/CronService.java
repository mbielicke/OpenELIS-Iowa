package org.openelis.modules.cron.server;

import java.util.ArrayList;

import org.openelis.domain.CronDO;
import org.openelis.domain.IdNameVO;
import org.openelis.gwt.common.data.Query;
import org.openelis.server.EJBFactory;

public class CronService {
    
	public CronDO fetchById(Integer id) throws Exception {
        return EJBFactory.getCron().fetchById(id);
    }

    public ArrayList<IdNameVO> query(Query query) throws Exception {
        return EJBFactory.getCron().query(query.getFields(), query.getPage() * query.getRowsPerPage(), query.getRowsPerPage());
    }

    public CronDO add(CronDO data) throws Exception {
        return EJBFactory.getCron().add(data);
    }

    public CronDO update(CronDO data) throws Exception {
        return EJBFactory.getCron().update(data);
    }

    public CronDO fetchForUpdate(Integer id) throws Exception {
        return EJBFactory.getCron().fetchForUpdate(id);
    }

    public void delete(CronDO data) throws Exception {
        EJBFactory.getCron().delete(data);
    }

    public CronDO abortUpdate(Integer id) throws Exception {
        return EJBFactory.getCron().abortUpdate(id);
    }
}
