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

import org.openelis.bean.TestTypeOfSampleBean;
import org.openelis.domain.TestTypeOfSampleDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.utils.EJBFactory;

public class TestTypeOfSampleManagerProxy {   
    
    public TestTypeOfSampleManager fetchByTestId(Integer testId) throws Exception {
        ArrayList<TestTypeOfSampleDO> sampleTypes;
        TestTypeOfSampleManager man;
                       
        sampleTypes = EJBFactory.getTestTypeOfSample().fetchByTestId(testId);
        man = TestTypeOfSampleManager.getInstance();
        man.setTypes(sampleTypes);
        man.setTestId(testId);
        
        return man;       
    }
    
    public TestTypeOfSampleManager add(TestTypeOfSampleManager man) throws Exception {
        int i;
        TestTypeOfSampleBean tl; 
        TestTypeOfSampleDO data;
        
        tl = EJBFactory.getTestTypeOfSample();         
        
        for(i = 0; i < man.count(); i++){
            data = man.getTypeAt(i);
            
            data.setTestId(man.getTestId());
            tl.add(data);            
        }
        return man;
    }
    
    public TestTypeOfSampleManager update(TestTypeOfSampleManager man) throws Exception {
        int i;
        TestTypeOfSampleBean tl; 
        TestTypeOfSampleDO data;
        
        tl = EJBFactory.getTestTypeOfSample(); 
        for(i = 0; i < man.deleteCount(); i++){
            tl.delete(man.getDeletedAt(i));
        }
        
        for(i = 0; i < man.count(); i++){
            data = man.getTypeAt(i);
            
            if(data.getId() == null){
                data.setTestId(man.getTestId());
                tl.add(data);
            }else
                tl.update(data);
        }

        return man;
    }   
    
    public void validate(TestTypeOfSampleManager man) throws Exception {
        int count;
        ValidationErrorsList list;
        TestTypeOfSampleDO data;
        TestTypeOfSampleBean sl;

        list = new ValidationErrorsList();
        sl = EJBFactory.getTestTypeOfSample();
        count = man.count();
        if (count == 0) {
            list.add(new FieldErrorException("atleastOneSampleTypeException", null));
        } else {
            for (int i = 0; i < count; i++ ) {
                data = man.getTypeAt(i);

                try {
                    sl.validate(data);
                } catch (Exception e) {
                    DataBaseUtil.mergeException(list, e, "sampleTypeTable", i);
                }
            }
        }
        
        if(list.size() > 0)
            throw list;
    }
}
