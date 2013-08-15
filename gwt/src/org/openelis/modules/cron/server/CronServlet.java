package org.openelis.modules.cron.server;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.CronBean;
import org.openelis.domain.CronDO;
import org.openelis.domain.IdNameVO;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.server.RemoteServlet;
import org.openelis.modules.cron.client.CronServiceInt;

@WebServlet("/openelis/cron")
public class CronServlet extends RemoteServlet implements CronServiceInt {
    
    private static final long serialVersionUID = 1L;
    
    @EJB
    CronBean cron;

    public CronDO fetchById(Integer id) throws Exception {
        try {        
            return cron.fetchById(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public ArrayList<IdNameVO> query(Query query) throws Exception {
        try {        
            return cron.query(query.getFields(), query.getPage() * query.getRowsPerPage(), query.getRowsPerPage());
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public CronDO add(CronDO data) throws Exception {
        try {        
            return cron.add(data);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public CronDO update(CronDO data) throws Exception {
        try {        
            return cron.update(data);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public CronDO fetchForUpdate(Integer id) throws Exception {
        try {        
            return cron.fetchForUpdate(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public void delete(CronDO data) throws Exception {
        try {        
            cron.delete(data);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public CronDO abortUpdate(Integer id) throws Exception {
        try {        
            return cron.abortUpdate(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
}
