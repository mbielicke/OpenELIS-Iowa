package org.openelis.modules.cron.server;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.CronBean;
import org.openelis.domain.CronDO;
import org.openelis.domain.IdNameVO;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.server.RemoteServlet;
import org.openelis.modules.cron.client.CronServiceInt;

@WebServlet("/openelis/cron")
public class CronServlet extends RemoteServlet implements CronServiceInt {
    
    private static final long serialVersionUID = 1L;
    
    @EJB
    CronBean cron;

    public CronDO fetchById(Integer id) throws Exception {
        return cron.fetchById(id);
    }

    public ArrayList<IdNameVO> query(Query query) throws Exception {
        return cron.query(query.getFields(), query.getPage() * query.getRowsPerPage(), query.getRowsPerPage());
    }

    public CronDO add(CronDO data) throws Exception {
        return cron.add(data);
    }

    public CronDO update(CronDO data) throws Exception {
        return cron.update(data);
    }

    public CronDO fetchForUpdate(Integer id) throws Exception {
        return cron.fetchForUpdate(id);
    }

    public void delete(CronDO data) throws Exception {
        cron.delete(data);
    }

    public CronDO abortUpdate(Integer id) throws Exception {
        return cron.abortUpdate(id);
    }
}
