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
package org.openelis.remote;

import org.openelis.domain.LabelDO;
import org.openelis.gwt.common.data.deprecated.AbstractField;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Remote;

@Remote
public interface LabelRemote {
    // method to return Label 
    public LabelDO getLabel(Integer labelId);
    
    public LabelDO getLabelAndUnlock(Integer labelId, String session);
    
    public LabelDO getLabelAndLock(Integer labelId, String session)throws Exception;    
     
    //  commit a change to Label or insert a new Label
    public Integer updateLabel(LabelDO labelDO)throws Exception;
    
    //  method to query for Label
    public List query(ArrayList<AbstractField> fields, int first, int max) throws Exception;
    
    public void deleteLabel(LabelDO labelDO) throws Exception;
    
    public List getLabelAutoCompleteByName(String match,int maxResults);
}
