/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.modules.qc.server;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.QcBean;
import org.openelis.bean.QcLotBean;
import org.openelis.bean.QcManagerBean;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.QcDO;
import org.openelis.domain.QcLotDO;
import org.openelis.domain.QcLotViewDO;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.server.AppServlet;
import org.openelis.manager.QcAnalyteManager;
import org.openelis.manager.QcLotManager;
import org.openelis.manager.QcManager;
import org.openelis.modules.qc.client.QcServiceInt;

@WebServlet("/openelis/qc")
public class QcServlet extends AppServlet implements QcServiceInt {

    private static final long serialVersionUID = 1L;
    
    @EJB
    QcManagerBean qcManager;
    
    @EJB
    QcBean        qc;
    
    @EJB
    QcLotBean     qcLot;

    public QcManager fetchById(Integer id) throws Exception {
        return qcManager.fetchById(id);
    }

    public ArrayList<QcDO> fetchByName(String search) throws Exception {
        ArrayList<QcDO> list;

        try {
            list = qc.fetchByName(search + "%", 10);
        } catch (NotFoundException e) {
            list = new ArrayList<QcDO>(0);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
        return list;
    }
    
    public ArrayList<QcLotViewDO> fetchActiveByName(Query query) throws Exception {
        return qc.fetchActiveByName(query.getFields());
    }

    public ArrayList<QcLotViewDO> fetchActiveByName(String search) throws Exception {
        ArrayList<QcLotViewDO> list;

        try {
            list = qc.fetchActiveByName(search + "%", 10);
        } catch (NotFoundException e) {
            list = new ArrayList<QcLotViewDO>(0);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
        return list;
    }

    public QcManager fetchWithAnalytes(Integer id) throws Exception {
        return qcManager.fetchWithAnalytes(id);
    }
    
    public QcManager fetchWithLots(Integer id) throws Exception {
        return qcManager.fetchWithLots(id);
    }
    
    public QcLotViewDO fetchLotById(Integer id) throws Exception {
        return qcLot.fetchById(id);
    }

    public ArrayList<IdNameVO> query(Query query) throws Exception {
        return qc.query(query.getFields(), query.getPage() * query.getRowsPerPage(), query.getRowsPerPage());
    }

    public QcManager add(QcManager man) throws Exception {
        return qcManager.add(man);
    }

    public QcManager update(QcManager man) throws Exception {
        return qcManager.update(man);
    }

    public QcManager fetchForUpdate(Integer id) throws Exception {
        return qcManager.fetchForUpdate(id);
    }

    public QcManager abortUpdate(Integer id) throws Exception {
        return qcManager.abortUpdate(id);
    }
    
    public QcManager duplicate(Integer id) throws Exception {
        return qcManager.duplicate(id);
    }

    //
    // support for QcAnalyteManager and QcLotManager
    //
    public QcAnalyteManager fetchAnalyteByQcId(Integer id) throws Exception {
        return qcManager.fetchAnalyteByQcId(id);
    }
    
    public QcLotManager fetchLotByQcId(Integer id) throws Exception {
        return qcManager.fetchLotByQcId(id);
    }
    
    public QcLotDO validateForDelete(QcLotViewDO data) throws Exception {
        qcLot.validateForDelete(data);
        return data;
    } 
}