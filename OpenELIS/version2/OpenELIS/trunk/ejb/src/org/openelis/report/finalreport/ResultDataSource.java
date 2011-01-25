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
package org.openelis.report.finalreport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.naming.InitialContext;

import org.openelis.domain.ResultViewDO;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.local.DictionaryLocal;
import org.openelis.local.ResultLocal;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

public class ResultDataSource implements JRDataSource {
   
    private ArrayList<HashMap<String, String>> rows;        
    private HashMap<String, String> currentRow;
    private Iterator<HashMap<String, String>> iter;
    private static Integer typeDictionary;
    private static HashMap<Integer, String> dictValueMap;
    
    private ResultDataSource() {        
    }
    
    private ResultDataSource(Integer analysisId, boolean byGroup) throws Exception {
        ArrayList<ArrayList<ResultViewDO>> results;
        HashMap<String, String> row;
        
        try {
            results = local().fetchReportableByAnalysisId(analysisId);
        } catch (Exception e) {               
            throw e;
        }
        
        if (typeDictionary == null) 
            typeDictionary = dictLocal().fetchBySystemName("test_res_type_dictionary").getId();
        if (dictValueMap == null)
            dictValueMap = new HashMap<Integer, String>();
        
        rows = new ArrayList<HashMap<String, String>>();
        for (int i = 0; i < results.size(); i++) {
            row = createRow(results.get(i));
            if (row != null)
                rows.add(row);
        }     
                
        iter = rows.iterator();       
    }
    
    public static ResultDataSource getInstance(Integer analysisId, boolean byGroup) throws Exception {
        ResultDataSource ds;
        ds = null;
        try {
            ds = new ResultDataSource(analysisId, byGroup);            
        } catch (NotFoundException e) {
            ds = new ResultDataSource();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        
        return ds;
    }
    
    public Object getFieldValue(JRField field) throws JRException {
        return currentRow.get(field.getName());
    }

    public boolean next() throws JRException {        
        if (rows == null)
            return false;
        
        if (iter.hasNext()) {
            currentRow = iter.next();
            return true;
        }
        return false;
    }    
        
    private ResultLocal local() {
        try {
            InitialContext ctx = new InitialContext();
            return (ResultLocal)ctx.lookup("openelis/ResultBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    
    private DictionaryLocal dictLocal() {
        try {
            InitialContext ctx = new InitialContext();
            return (DictionaryLocal)ctx.lookup("openelis/DictionaryBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }    
    
    private HashMap<String, String> createRow(ArrayList<ResultViewDO> list) {
        int i;
        boolean hasRowAnalyte;
        Integer id;
        HashMap<String, String> row;
        ResultViewDO data;
        String value;
        DictionaryLocal dl;        
        
        row = null;
        data = list.get(0);
        hasRowAnalyte = false;
                
        /*
         * we need to check to see if there is a row analyte present for a result 
         * in a given row in a row group, if this is not the case, we don't 
         * add the row containing those results to the structure that supplies
         * the data for the report      
         */
        if ("N".equals(data.getIsColumn())) {
            row = new HashMap<String, String>();
            row.put("field0",data.getAnalyte());
            hasRowAnalyte = true;
        }        
                 
        if (!hasRowAnalyte) 
            return row;
        
        //
        // add the row analyte and values
        //
        dl = dictLocal();
        for (i = 1; i < list.size() + 1; i++ ) {
            data = list.get(i - 1);
            value = data.getValue();
            try {
                if (typeDictionary.equals(data.getTypeId())) {
                    id = Integer.parseInt(value);
                    value = dictValueMap.get(id);
                    if (value == null) {
                        value = dl.fetchById(id).getEntry();
                        dictValueMap.put(id, value);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                value = "";
            }
            row.put("field" + i, value);
        }               
        
        row.put("rowGroup", data.getRowGroup().toString());
        //
        // add the text for the headers / column analytes
        //
        row.put("header0", "Analyte");
        row.put("header1", "Value");
        for (i = 2; i < list.size() + 1; i++) 
            row.put("header" + i, list.get(i - 1).getAnalyte());        
        
        return row;
        
    }

}