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
package org.openelis.manager;

import java.util.ArrayList;
import java.util.HashSet;

import org.openelis.bean.QcLotBean;
import org.openelis.constants.Messages;
import org.openelis.domain.QcLotDO;
import org.openelis.domain.QcLotViewDO;
import org.openelis.meta.QcMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.TableFieldErrorException;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.utils.EJBFactory;

public class QcLotManagerProxy {

    public QcLotManager fetchByQcId(Integer id) throws Exception {
        QcLotManager cm;
        ArrayList<QcLotViewDO> lot;

        lot = EJBFactory.getQcLot().fetchByQcId(id);
        cm = QcLotManager.getInstance();
        cm.setQcId(id);
        cm.setLots(lot);

        return cm;
    }

    public QcLotManager add(QcLotManager man) throws Exception {
        QcLotBean cl;
        QcLotViewDO lot;

        cl = EJBFactory.getQcLot();
        for (int i = 0; i < man.count(); i++ ) {
            lot = man.getLotAt(i);
            lot.setQcId(man.getQcId());
            cl.add(lot);
        }

        return man;
    }

    public QcLotManager update(QcLotManager man) throws Exception {
        QcLotBean cl;
        QcLotViewDO lot;

        cl = EJBFactory.getQcLot();
        for (int j = 0; j < man.deleteCount(); j++ )
            cl.delete(man.getDeletedAt(j));

        for (int i = 0; i < man.count(); i++ ) {
            lot = man.getLotAt(i);
            if (lot.getId() == null) {
                lot.setQcId(man.getQcId());
                cl.add(lot);
            } else {
                cl.update(lot);
            }
        }

        return man;
    }

    public void validate(QcLotManager man) throws Exception {
        int i;
        String lotNum;
        QcLotViewDO data;
        QcLotDO lot;
        ValidationErrorsList list;
        QcLotBean cl;
        HashSet<String> lotNums;

        cl = EJBFactory.getQcLot();
        list = new ValidationErrorsList();
        lotNums = new HashSet<String>();
        
        for(i = 0;  i < man.deleteCount(); i++) {
            data = man.getDeletedAt(i);
            try {
                cl.validateForDelete(data);
            } catch(ValidationErrorsList  e) {
                list.add(e.getErrorList().get(0));
            } catch(Exception e) {
                DataBaseUtil.mergeException(list, e);                
            } 
        }
        
        for (i = 0; i < man.count(); i++ ) {
            data = man.getLotAt(i);
            try {
                cl.validate(data);
            } catch (Exception e) {
                DataBaseUtil.mergeException(list, e, "qcLotTable", i);
            }
            
            lotNum = data.getLotNumber();
            if (lotNum == null)
                continue;
            
            if (!lotNums.contains(lotNum)) {
                try {
                    lot = cl.fetchByLotNumber(lotNum);
                    if (!lot.getQcId().equals(data.getQcId())) {
                        list.add(new TableFieldErrorException(Messages.get().fieldUniqueException(), i,
                                                              QcMeta.getQcLotLotNumber(), "qcLotTable"));                        
                   }
                } catch (NotFoundException e) {
                    //do nothing               
                }

                lotNums.add(lotNum);
             } else {
                 list.add(new TableFieldErrorException(Messages.get().fieldUniqueOnlyException(), i,
                                                           QcMeta.getQcLotLotNumber(), "qcLotTable"));                        
             }
        }
        if (list.size() > 0)
            throw list;
    }
}