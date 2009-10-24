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
package org.openelis.modules.section.server;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.SectionViewDO;
import org.openelis.domain.SystemVariableDO;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.data.Query;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.SectionRemote;


public class SectionService {

    private static final int rowPP = 9;
    
    public SectionViewDO fetchById(Integer id) throws Exception {
        try {
            return remote().fetchById(id);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }
    
    public ArrayList<IdNameVO> query(Query query) throws Exception {
        try {
            return remote().query(query.getFields(), query.getPage() * rowPP, rowPP);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }
    
    public SectionViewDO add(SectionViewDO data) throws Exception {
        try {
            return remote().add(data);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }
    
    public SectionViewDO update(SectionViewDO data) throws Exception {
        try {
            return remote().update(data);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }
    
    public SectionViewDO fetchForUpdate(Integer id) throws Exception {
        try {
            return remote().fetchForUpdate(id);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }
    
    public SectionViewDO abortUpdate(Integer id) throws Exception {
        try {
            return remote().abortUpdate(id);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }
    
    private SectionRemote remote() {
        return (SectionRemote)EJBFactory.lookup("openelis/SectionBean/remote"); 
    }
    
    public ArrayList<IdNameVO> fetchByName(Query query) throws Exception {             
        String value;
 
        value = query.getFields().get(0).query;        
        
        return remote().fetchByName(value+"%", 10);   
    }
    
}
