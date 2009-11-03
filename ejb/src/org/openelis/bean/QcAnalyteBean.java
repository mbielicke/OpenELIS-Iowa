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
package org.openelis.bean;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.DictionaryViewDO;
import org.openelis.domain.QcAnalyteDO;
import org.openelis.domain.QcAnalyteViewDO;
import org.openelis.entity.QcAnalyte;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.QcAnalyteLocal;
import org.openelis.meta.QcAnalyteMeta;
import org.openelis.utilcommon.DataBaseUtil;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("organization-select")
public class QcAnalyteBean implements QcAnalyteLocal {

    @PersistenceContext(name = "openelis")
    private EntityManager                    manager;

    private static final QcAnalyteMeta meta = new QcAnalyteMeta();

    @SuppressWarnings("unchecked")
    public ArrayList<QcAnalyteViewDO> fetchByQcId(Integer id) throws Exception {
        Query query;
        List list;
        QcAnalyteViewDO data;
        Integer typeId, val;
        DictionaryViewDO snDO,entDO;      
        String sysName, entry;

        query = manager.createNamedQuery("QcAnalyte.FetchByQcId");
        query.setParameter("id", id);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();
        
        try {                       
            for (int i = 0 ; i < list.size(); i++) {
                data = (QcAnalyteViewDO)list.get(i);                
                typeId = data.getTypeId();
                query = manager.createNamedQuery("Dictionary.FetchById");
                query.setParameter("id", typeId);
                snDO = (DictionaryViewDO)query.getResultList().get(0);
                sysName = snDO.getSystemName();
                if ("qc_analyte_dictionary".equals(sysName)) {
                    val = Integer.parseInt(data.getValue());
                    query = manager.createNamedQuery("Dictionary.FetchById");
                    query.setParameter("id", val);
                    entDO = (DictionaryViewDO)query.getResultList().get(0);
                    entry = entDO.getEntry();
                    data.setDictionary(entry);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return DataBaseUtil.toArrayList(list);
    }

    public QcAnalyteViewDO add(QcAnalyteViewDO data) throws Exception {
        QcAnalyte entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new QcAnalyte();
        entity.setQcId(data.getQcId());
        entity.setAnalyteId(data.getAnalyteId());
        entity.setTypeId(data.getTypeId());
        entity.setValue(data.getValue());
        entity.setIsTrendable(data.getIsTrendable());

        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }

    public QcAnalyteViewDO update(QcAnalyteViewDO data) throws Exception {
        QcAnalyte entity;

        if ( !data.isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(QcAnalyte.class, data.getId());
        entity.setQcId(data.getQcId());
        entity.setAnalyteId(data.getAnalyteId());
        entity.setTypeId(data.getTypeId());
        entity.setValue(data.getValue());
        entity.setIsTrendable(data.getIsTrendable());

        return data;
    }

    public void delete(QcAnalyteDO data) throws Exception {
        QcAnalyte entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(QcAnalyte.class, data.getId());
        if (entity != null)
            manager.remove(entity);
    }

    public void validate(QcAnalyteDO data) throws Exception {
        ValidationErrorsList list;

        list = new ValidationErrorsList();
        if (DataBaseUtil.isEmpty(data.getAnalyteId()))
            list.add(new FieldErrorException("fieldRequiredException",
                                             meta.getAnalyteId()));
        if (DataBaseUtil.isEmpty(data.getTypeId()))
            list.add(new FieldErrorException("fieldRequiredException",
                                             meta.getTypeId()));
        if (DataBaseUtil.isEmpty(data.getValue()))
            list.add(new FieldErrorException("fieldRequiredException",
                                             meta.getValue()));
        if (list.size() > 0)
            throw list;
    }
}

/*
private void validateQcAnalytes(List<QcAnalyteViewDO> qcAnaDOList, ValidationErrorsList exceptionList) {
QcAnalyteViewDO qcaDO;
Integer numId,dictId,titerId,typeId,entryId;
String value, fieldName;
NumericRange nr;
TiterRange tr;
List<Integer> dictList;
List<TiterRange> trlist;
List<NumericRange> nrlist;
TableFieldErrorException exc;
int i;                

value = null;             
         
dictId = categoryBean.getEntryIdForSystemName("qc_analyte_dictionary");                 
numId = categoryBean.getEntryIdForSystemName("qc_analyte_numeric");                
titerId = categoryBean.getEntryIdForSystemName("qc_analyte_titer");

trlist = new ArrayList<TiterRange>();
nrlist = new ArrayList<NumericRange>();
dictList = new ArrayList<Integer>();

for(i = 0; i < qcAnaDOList.size(); i++) {           
    qcaDO = qcAnaDOList.get(i);               
    //if(qcaDO.getDelete())
   //     continue;                                                 
    
    value = qcaDO.getValue();               
    typeId = qcaDO.getTypeId();
    //
    // units need to be valid for every result type because
    // their use is dependent on the unit
    //
    
    fieldName = QcMeta.getQcAnalyte().getValue();
    //
    // dictionary, titers, numeric require a value
    //
    if (value == null || "".equals(value)) {
        exc = new TableFieldErrorException("fieldRequiredException", i,fieldName);
        exceptionList.add(exc);  
        continue;
    }
                              
    try {
        if (numId.equals(typeId)) {                  
            nr = new NumericRange(value);
            addNumericIfNoOverLap(nrlist, nr);
        } else if (titerId.equals(typeId)) {                   
            tr = new TiterRange(value);
            addTiterIfNoOverLap(trlist,tr);
        } else if (dictId.equals(typeId)) {
            entryId = categoryBean.getEntryIdForEntry(value);
            if (entryId == null)
                throw new ParseException("illegalDictEntryException");

            if (!dictList.contains(entryId))
                dictList.add(entryId);
            else
                throw new InconsistencyException("qcDictEntryNotUniqueException");                                              
        } else {
            fieldName = QcMeta.getQcAnalyte().getTypeId();
            throw new ParseException("fieldRequiredException");
        }
    } catch (ParseException ex) {                 
        exc = new TableFieldErrorException(ex.getMessage(), i,fieldName);
        exceptionList.add(exc); 
    } catch (InconsistencyException ex) {               
        exc = new TableFieldErrorException(ex.getMessage(), i,fieldName);
        exceptionList.add(exc);
    }                                                
 }
}

private void addNumericIfNoOverLap(List<NumericRange> nrList,
                               NumericRange nr) throws InconsistencyException{
 NumericRange lr;                  
 
 for(int i = 0; i < nrList.size(); i++) {
     lr = nrList.get(i);
     if(lr.isOverlapping(nr))                    
         throw new InconsistencyException("qcNumRangeOverlapException");             
 }
 
 nrList.add(nr);         
}

private void addTiterIfNoOverLap(List<TiterRange> trList,
                               TiterRange tr) throws InconsistencyException{
 TiterRange lr;                  
 
 for(int i = 0; i < trList.size(); i++) {
     lr = trList.get(i);
     if(lr.isOverlapping(tr))                    
         throw new InconsistencyException("qcTiterRangeOverlapException");             
 }
 
 trList.add(tr);         
}


*/