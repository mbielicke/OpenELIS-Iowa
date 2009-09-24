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
import java.util.HashMap;

import javax.naming.InitialContext;

import org.openelis.domain.TestWorksheetAnalyteViewDO;
import org.openelis.domain.TestWorksheetItemDO;
import org.openelis.domain.TestWorksheetViewDO;
import org.openelis.local.TestLocal;


public class TestWorksheetManagerProxy {

    public TestWorksheetManager add(TestWorksheetManager man,
                                    HashMap<Integer,Integer> anaIdMap) throws Exception {
        TestLocal tl;
        TestWorksheetViewDO worksheet;
        TestWorksheetItemDO item;
        TestWorksheetAnalyteViewDO analyte;
        int i;
        Integer id;
           
        worksheet = man.getWorksheet();
        tl = getTestLocal();
        
        //
        // This check is put here in order to distinguish between the cases where 
        // the TestWorksheetDO was changed on the screen and where it was not.
        // This is necessary because it is possible for the users to enter no 
        // information on the screen in the fields related to the DO and 
        // commit the data and the DO can't be null because then the fields
        // on the screen won't get refreshed on fetch or when the screen goes 
        // into the add mode. The _changed flag will get set if any of the fields
        // was changed on the screen. The validation code won't be executed in the 
        // _changed flag isn't set for the same reason.
        //
        if(worksheet.isChanged()) {
            worksheet.setTestId(man.getTestId());        
            tl.addTestWorksheet(worksheet);
        }
        
        for(i = 0; i < man.itemCount(); i++) {
            item = man.getItemAt(i);
            item.setTestWorksheetId(worksheet.getId());
            
            tl.addTestWorksheetItem(item);
        }
        
        for(i = 0; i < man.analyteCount(); i++) {
            analyte = man.getAnalyteAt(i);
            id = analyte.getTestAnalyteId();  
            if(id < 0)
                analyte.setTestAnalyteId(anaIdMap.get(id));
            analyte.setTestId(man.getTestId());
            tl.addTestWorksheetAnalyte(analyte);
        }
        
        return man;        
    }
    
    public TestWorksheetManager update(TestWorksheetManager man,
                                       HashMap<Integer,Integer> anaIdMap) throws Exception {
        TestLocal tl;
        TestWorksheetViewDO worksheet;
        TestWorksheetItemDO item;
        TestWorksheetAnalyteViewDO analyte;
        int i;
        Integer id; 
                
        worksheet = man.getWorksheet();
        tl = getTestLocal();
        
        //
        // This check for the _changed flag is put here in order to distinguish
        // between the cases where the TestWorksheetDO was changed on the screen 
        // and where it was not. This is necessary because it is possible for the
        // users to enter no information on the screen in the fields related to
        // the DO and commit the data and the DO can't be null because then the 
        // fields on the screen won't get refreshed on fetch or when the screen 
        // goes into the add mode. The _changed flag will get set if any of the
        // fields was changed on the screen. The validation code won't be 
        // executed in the _changed flag isn't set for the same reason.
        //
        if(worksheet.getId() == null && worksheet.isChanged()) {            
            worksheet.setTestId(man.getTestId());
            tl.addTestWorksheet(worksheet);
        } else {
            tl.updateTestWorksheet(worksheet);
        }
        
        for(i = 0; i < man.deleteItemCount(); i++) {            
            tl.deleteTestWorksheetItem(man.getDeletedItemAt(i));
        }
        
        for(i = 0; i < man.itemCount(); i++) {
            item = man.getItemAt(i);            
            if(item.getId() == null) {
                item.setTestWorksheetId(worksheet.getId()); 
                tl.addTestWorksheetItem(item);
            } else {
                tl.updateTestWorksheetItem(item);
            }            
        } 
        
        for(i = 0; i < man.deleteAnalyteCount(); i++) {            
            tl.deleteTestWorksheetAnalyte(man.getDeletedAnalyteAt(i));
        }
        
        for(i = 0; i < man.analyteCount(); i++) {
            analyte = man.getAnalyteAt(i);
            id = analyte.getTestAnalyteId();  
            if(id < 0)
                analyte.setTestAnalyteId(anaIdMap.get(id));
            
            if(analyte.getId() == null) {
                analyte.setTestId(man.getTestId());
                tl.addTestWorksheetAnalyte(analyte);
            } else {
                tl.updateTestWorksheetAnalyte(analyte);
            }
            
        }
        
        return man;        
    }
    
    public TestWorksheetManager fetchByTestId(Integer testId) throws Exception {
        TestLocal tl;
        TestWorksheetManager twm;
        TestWorksheetViewDO wsDO;
        ArrayList<TestWorksheetItemDO> items;
        ArrayList<TestWorksheetAnalyteViewDO> analytes;
       
        items = null;
        tl = getTestLocal();
        twm = TestWorksheetManager.getInstance();
        wsDO = tl.getTestWorksheet(testId);
        
        if(wsDO ==null)
            wsDO = new TestWorksheetViewDO();        
        
        items = tl.getTestWorksheetItems(wsDO.getId());        
        analytes = tl.getTestWorksheetAnalytes(testId);  
        
        twm.setTestId(testId);
        twm.setWorksheet(wsDO);
        twm.setItems(items);
        twm.setAnalytes(analytes);
        
        return twm;
    }  
    
    private TestLocal getTestLocal(){
        try{
            InitialContext ctx = new InitialContext();
            return (TestLocal)ctx.lookup("openelis/TestBean/local");
        }catch(Exception e){
             System.out.println(e.getMessage());
             return null;
        }
    }
}
