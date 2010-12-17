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

import javax.naming.InitialContext;

import org.openelis.domain.SampleSDWISViewDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.PwsLocal;
import org.openelis.local.SampleSDWISLocal;
import org.openelis.meta.SampleMeta;

public class SampleSDWISManagerProxy {
    public SampleSDWISManager fetchBySampleId(Integer sampleId) throws Exception {
        SampleSDWISViewDO data;
        SampleSDWISManager man;

        data = local().fetchBySampleId(sampleId);
        man = SampleSDWISManager.getInstance();

        man.setSDWIS(data);

        return man;
    }

    public SampleSDWISManager add(SampleSDWISManager man) throws Exception {
        man.getSDWIS().setSampleId(man.getSampleId());
        local().add(man.getSDWIS());

        return man;
    }

    public SampleSDWISManager update(SampleSDWISManager man) throws Exception {
        SampleSDWISViewDO data;

        data = man.getSDWIS();

        if (data.getId() == null) {
            data.setSampleId(man.getSampleId());
            local().add(data);
        } else
            local().update(data);

        return man;
    }

    public void validate(SampleSDWISManager man, ValidationErrorsList errorsList) throws Exception {
        ValidationErrorsList list;
        
        list = new ValidationErrorsList();
        try {
            local().validate(man.getSDWIS());
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
        }
        
        if (list.size() > 0)
            throw list;
    }

    private SampleSDWISLocal local() {
        try {
            InitialContext ctx = new InitialContext();
            return (SampleSDWISLocal)ctx.lookup("openelis/SampleSDWISBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private PwsLocal pwsLocal() {
        try {
            InitialContext ctx = new InitialContext();
            return (PwsLocal)ctx.lookup("openelis/PwsBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
