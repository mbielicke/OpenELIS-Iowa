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

import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.services.CalendarService;
import org.openelis.manager.SampleItemManager.SampleItemListItem;
import org.openelis.modules.sample.client.SampleService;

public class SampleItemManagerProxy {
    
    public SampleItemManagerProxy(){
    }
    
    public SampleItemManager fetchBySampleId(Integer sampleId) throws Exception {
        return SampleService.get().fetchSampleItemsBySampleId(sampleId);
    }
    
    public SampleItemManager add(SampleItemManager man) throws Exception {
        assert false : "not supported";
        return null;
    }

    public SampleItemManager update(SampleItemManager man) throws Exception {
        assert false : "not supported";
        return null;
    }

    public void validate(SampleItemManager man, ValidationErrorsList errorsList) throws Exception {
        int i;
        String sequenceNum;
        SampleItemListItem item;
        //you have to have at least 1 sample item
        if (man.count() == 0)
            errorsList.add(new FormErrorException("minOneSampleItemException"));
        
        for (i = 0; i < man.count(); i++) {
            sequenceNum = man.getSampleItemAt(i).getItemSequence().toString();
            //validate the sample item
            if (man.getSampleItemAt(i).getTypeOfSampleId() == null)
                errorsList.add(new FormErrorException("sampleItemTypeMissing", sequenceNum));
            
            item = man.getItemAt(i);
            //validate the children
            if (item.storage != null)
                man.getStorageAt(i).validate(errorsList);
            
            if (item.analysis != null)
                man.getAnalysisAt(i).validate(sequenceNum, man.getSampleItemAt(i).getTypeOfSampleId(), man.getSampleManager().getSample().getDomain(), errorsList);
        }
    }

    protected Datetime getCurrentDatetime(byte begin, byte end) throws Exception {
        return CalendarService.get().getCurrentDatetime(begin, end);
    }
}
