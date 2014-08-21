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

import javax.naming.InitialContext;

import org.openelis.domain.AnalysisQaEventViewDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.AnalysisQAEventLocal;
import org.openelis.local.DictionaryLocal;

public class AnalysisQAEventManagerProxy {
    public AnalysisQaEventManager fetchByAnalysisId(Integer analysisId) throws Exception {
        ArrayList<AnalysisQaEventViewDO> items;
        AnalysisQaEventManager aqm;
        
        items = local().fetchByAnalysisId(analysisId);
        aqm = AnalysisQaEventManager.getInstance();
        aqm.setAnalysisQAEvents(items);
        aqm.setAnalysisId(analysisId);
        
        return aqm;
    }
    
    public AnalysisQaEventManager add(AnalysisQaEventManager man) throws Exception {
        AnalysisQaEventViewDO analysisQADO;
        AnalysisQAEventLocal l;
        
        l = local();
        for(int i=0; i<man.count(); i++){
            analysisQADO = man.getAnalysisQAAt(i);
            analysisQADO.setAnalysisId(man.getAnalysisId());
            
            l.add(analysisQADO);
        }
        
        return man;
    }
    
    public AnalysisQaEventManager update(AnalysisQaEventManager man) throws Exception {
        AnalysisQaEventViewDO analysisQADO;
        AnalysisQAEventLocal l;
        
        l = local();
        for(int j=0; j<man.deleteCount(); j++)
            l.delete(man.getDeletedAt(j));
        
        for(int i=0; i<man.count(); i++){
            analysisQADO = man.getAnalysisQAAt(i);
            
            if(analysisQADO.getId() == null){
                analysisQADO.setAnalysisId(man.getAnalysisId());
                l.add(analysisQADO);
            }else
                l.update(analysisQADO);
        }

        return man;
    }
    
    public void validate(AnalysisQaEventManager man, ValidationErrorsList errorsList) throws Exception {
        
    }
    
    public Integer getIdFromSystemName(String systemName) throws Exception{
        DictionaryDO dictDO = dictionaryLocal().fetchBySystemName(systemName);
        
        return dictDO.getId();
    }
    
    private AnalysisQAEventLocal local(){
        try{
            InitialContext ctx = new InitialContext();
            return (AnalysisQAEventLocal)ctx.lookup("openelis/AnalysisQAEventBean/local");
        }catch(Exception e){
             System.out.println(e.getMessage());
             return null;
        }
    }
    
    private static DictionaryLocal dictionaryLocal(){
        try{
            InitialContext ctx = new InitialContext();
            return (DictionaryLocal)ctx.lookup("openelis/DictionaryBean/local");
        }catch(Exception e){
             System.out.println(e.getMessage());
             return null;
        }
    }
}
