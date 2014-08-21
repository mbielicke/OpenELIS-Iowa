/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.manager;

import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;

import org.openelis.domain.InstrumentLogDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.InstrumentLogLocal;
import org.openelis.meta.InstrumentMeta;

public class InstrumentLogManagerProxy {
    
    public InstrumentLogManager fetchByInstrumentId(Integer id) throws Exception {
        InstrumentLogManager man;
        ArrayList<InstrumentLogDO> logs;
        
        logs = local().fetchByInstrumentId(id);
        man = InstrumentLogManager.getInstance();
        man.setInstrumentId(id);
        man.setLogs(logs);
        
        return man;
    }
    
    public InstrumentLogManager add(InstrumentLogManager man) throws Exception {
        InstrumentLogLocal il;
        InstrumentLogDO log;
        
        il = local();
        for (int i = 0; i < man.count(); i++ ) {
            log = man.getLogAt(i);
            log.setInstrumentId(man.getInstrumentId());
            il.add(log);
        }
        
        return man;
    }
    
    public InstrumentLogManager update(InstrumentLogManager man) throws Exception {
        InstrumentLogLocal il;
        InstrumentLogDO log;
        
        il = local();
        for (int j = 0; j < man.deleteCount(); j++ )
            il.delete(man.getDeletedAt(j));

        for (int i = 0; i < man.count(); i++ ) {
            log = man.getLogAt(i);

            if (log.getId() == null) {
                log.setInstrumentId(man.getInstrumentId());
                il.add(log);
            } else {
                il.update(log);
            }
        }

        return man;        
    }
    
    public void validate(InstrumentLogManager man) throws Exception {
        ValidationErrorsList list;
        
        list = new ValidationErrorsList();
        
        validateLog(list,man);
        
        if(list.size() > 0)
            throw list;
    }
    
    private void validateLog(ValidationErrorsList list, InstrumentLogManager man) {
        InstrumentLogLocal il;
        InstrumentLogDO data;
        TableFieldErrorException exc;
        Datetime eb,ee;
        int num;
        List<Datetime[]> rangeList;
        Datetime range[];
                
        num = 0;
        rangeList = new ArrayList<Datetime[]>();
        for(int i=0; i < man.count(); i++) {
            data = man.getLogAt(i);
            il = local();

            try {
                il.validate(data);
            } catch (Exception e) {               
                DataBaseUtil.mergeException(list, e, "logTable", i);
            }            

            ee = data.getEventEnd();
            eb = data.getEventBegin();            
            
            if(DataBaseUtil.isEmpty(ee)) {
                num++;                   
                if(num > 1) {
                    exc = new TableFieldErrorException("moreThanOneEndDateAbsentException", i,
                                                       InstrumentMeta.getLogEventEnd(),"logTable");                
                    list.add(exc);
                }
            }
            
            range = new Datetime[2];
            range[0] = eb;
            range[1] = ee;
            
            if(dateRangeOverlapping(range,rangeList)){
                exc = new TableFieldErrorException("intervalOverlapException", i,
                                                   InstrumentMeta.getLogEventBegin(),"logTable");                
                list.add(exc);
                exc = new TableFieldErrorException("intervalOverlapException", i,
                                                   InstrumentMeta.getLogEventEnd(),"logTable");                
                list.add(exc);
            } else {
                rangeList.add(range);
            }
             
        }
    }
    
    private boolean dateRangeOverlapping(Datetime[]range,List<Datetime[]> rangeList) {        
        Datetime[] lrange;        
        
        for(int i =0 ; i < rangeList.size(); i++) {
            lrange = rangeList.get(i);
            if(dateRangesOverlap(lrange[0], lrange[1], range[0], range[1])) 
                return true;             
        }
        
        return false; 
    }
    
    private boolean dateRangesOverlap(Datetime begin1, Datetime end1, 
                                  Datetime begin2, Datetime end2) {
        boolean overlap;

        overlap = false;
        
        if(begin1 == null || begin2 == null || end1 == null || end2 == null)
            return overlap;
        
        if(!DataBaseUtil.isDifferentYM(begin1,begin2) || !DataBaseUtil.isDifferentYM(begin1,end2) || 
                        !DataBaseUtil.isDifferentYM(end1,begin2) || !DataBaseUtil.isDifferentYM(end1,end2)){
            overlap = true;
        } else if((DataBaseUtil.isAfter(begin1,begin2) && DataBaseUtil.isAfter(end2, begin1))||
                        (DataBaseUtil.isAfter(begin2,begin1) && DataBaseUtil.isAfter(end1,begin2))){
            overlap = true;
        } else if((DataBaseUtil.isAfter(begin2,begin1) && DataBaseUtil.isAfter(end1,end2))
                        ||DataBaseUtil.isAfter(begin1,begin2) && DataBaseUtil.isAfter(end2,end1)){
            overlap = true;
        }
        
        return overlap; 
    }
    
    private InstrumentLogLocal local() {
        try {
            InitialContext ctx = new InitialContext();
            return (InstrumentLogLocal)ctx.lookup("openelis/InstrumentLogBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    
    }
}
 