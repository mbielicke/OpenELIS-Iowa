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
package org.openelis.bean;

import java.util.ArrayList;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.WorksheetCreationViewDO;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.metamap.WorksheetCreationMetaMap;
import org.openelis.remote.WorksheetCreationRemote;
import org.openelis.util.QueryBuilder;
import org.openelis.utils.GetPage;

@Stateless

@SecurityDomain("openelis")
@RolesAllowed("organization-select")
public class WorksheetCreationBean implements WorksheetCreationRemote {

	@PersistenceContext(name = "openelis")
    private EntityManager manager;
    
    private static final WorksheetCreationMetaMap WorksheetCreationMetaMap = new WorksheetCreationMetaMap();

    public WorksheetCreationBean() {
    }
	
	public ArrayList<WorksheetCreationViewDO> query(ArrayList<QueryData> fields,
                                     int first,
                                     int max) throws Exception {
	    ArrayList<WorksheetCreationViewDO> returnList;
        StringBuffer sb;
        QueryBuilder qb;
        Query query;
        
        sb = new StringBuffer();
        qb = new QueryBuilder();
        
        qb.setMeta(WorksheetCreationMetaMap);

        qb.setSelect("distinct new org.openelis.domain.WorksheetCreationViewDO(" +
                     WorksheetCreationMetaMap.SAMPLE.SAMPLE_ITEM.ANALYSIS.getId()+", "+
                     WorksheetCreationMetaMap.SAMPLE.getAccessionNumber()+", "+
                     WorksheetCreationMetaMap.SAMPLE.SAMPLE_ITEM.ANALYSIS.TEST.getName()+", "+
                     WorksheetCreationMetaMap.SAMPLE.SAMPLE_ITEM.ANALYSIS.TEST.METHOD.getName()+", "+
                     WorksheetCreationMetaMap.SAMPLE.SAMPLE_ITEM.ANALYSIS.SECTION.getName()+", "+
                     WorksheetCreationMetaMap.SAMPLE.SAMPLE_ITEM.ANALYSIS.getStatusId()+", "+
                     WorksheetCreationMetaMap.SAMPLE.getReceivedDate()+") ");

        // this method is going to throw an exception if a column doesn't match

        qb.addNewWhere(fields);

        qb.setOrderBy(WorksheetCreationMetaMap.SAMPLE.getAccessionNumber());

        sb.append(qb.getEJBQL());

        query = manager.createQuery(sb.toString());

        if (first > -1 && max > -1)
            query.setMaxResults(first + max);

        // ***set the parameters in the query
        qb.setNewQueryParams(query, fields);

        returnList = (ArrayList<WorksheetCreationViewDO>)GetPage.getPage(query.getResultList(),
                                                                         first,
                                                                         max);

        if (returnList == null)
            throw new LastPageException();
        else
            return returnList;
    }
}