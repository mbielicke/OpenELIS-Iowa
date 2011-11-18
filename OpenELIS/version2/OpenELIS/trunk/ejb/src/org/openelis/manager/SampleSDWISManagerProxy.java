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

import org.openelis.domain.SampleSDWISViewDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.utils.EJBFactory;

public class SampleSDWISManagerProxy {
    public SampleSDWISManager fetchBySampleId(Integer sampleId) throws Exception {
        SampleSDWISViewDO data;
        SampleSDWISManager man;

        data = EJBFactory.getSampleSDWIS().fetchBySampleId(sampleId);
        man = SampleSDWISManager.getInstance();

        man.setSDWIS(data);

        return man;
    }

    public SampleSDWISManager add(SampleSDWISManager man) throws Exception {
        man.getSDWIS().setSampleId(man.getSampleId());
        EJBFactory.getSampleSDWIS().add(man.getSDWIS());

        return man;
    }

    public SampleSDWISManager update(SampleSDWISManager man) throws Exception {
        SampleSDWISViewDO data;

        data = man.getSDWIS();

        if (data.getId() == null) {
            data.setSampleId(man.getSampleId());
            EJBFactory.getSampleSDWIS().add(data);
        } else
            EJBFactory.getSampleSDWIS().update(data);

        return man;
    }
    
    public void delete(SampleSDWISManager man) throws Exception {
        EJBFactory.getSampleSDWIS().delete(man.getSDWIS());
    }

    public void validate(SampleSDWISManager man, ValidationErrorsList errorsList) throws Exception {
        ValidationErrorsList list;
        
        list = new ValidationErrorsList();
        try {
            EJBFactory.getSampleSDWIS().validate(man.getSDWIS());
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
        }
        
        if (list.size() > 0)
            throw list;
    }
}