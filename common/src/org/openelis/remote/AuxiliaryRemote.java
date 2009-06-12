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

package org.openelis.remote;

import org.openelis.domain.AuxFieldDO;
import org.openelis.domain.AuxFieldGroupDO;
import org.openelis.domain.AuxFieldValueDO;
import org.openelis.domain.IdNameDO;
import org.openelis.gwt.common.data.AbstractField;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Remote;

@Remote
public interface AuxiliaryRemote {
        
  public AuxFieldGroupDO getAuxFieldGroup(Integer auxFieldGroupId);
  
  public List<AuxFieldDO> getAuxFields(Integer auxFieldGroupId);
  
  public List<AuxFieldValueDO> getAuxFieldValues(Integer auxFieldId);
  
  public List query(ArrayList<AbstractField> fields, int first, int max) throws Exception;
 
  public Integer updateAuxiliary(AuxFieldGroupDO auxFieldGroupDO,
                                 List<AuxFieldDO> auxFields)throws Exception;
  
  public AuxFieldGroupDO getAuxFieldGroupAndUnlock(Integer auxFieldGroupId,String session);
  
  public AuxFieldGroupDO getAuxFieldGroupAndLock(Integer auxFieldGroupId,
                                                 String session) throws Exception ;

  public List getMatchingEntries(String name,int maxResults,String cat);

  public List<IdNameDO> getScriptletDropDownValues();
}
