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
package org.openelis.modules.test.client;

import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.IntegerField;
import org.openelis.gwt.common.data.TableField;
import org.openelis.metamap.TestMetaMap;

import com.google.gwt.xml.client.Node;

public class WorksheetForm extends Form {

    /**
     * 
     */
    private static final long serialVersionUID = -2134012961429860507L;
    
    public Integer id;
    public DropDownField<Integer> formatId;
    public DropDownField<Integer> scriptletId;
    public IntegerField batchCapacity;
    public IntegerField totalCapacity;
    public TableField<Integer> worksheetTable;
    public TableField<Integer> worksheetAnalyteTable;
    public Boolean duplicate;
    
    public WorksheetForm() {
        TestMetaMap meta = new TestMetaMap();
        fields.put(meta.getTestWorksheet().getFormatId(), formatId = new DropDownField<Integer>());
        fields.put(meta.getTestWorksheet().getScriptletId(), scriptletId = new DropDownField<Integer>());
        fields.put(meta.getTestWorksheet().getBatchCapacity(), batchCapacity = new IntegerField());
        fields.put(meta.getTestWorksheet().getTotalCapacity(), totalCapacity = new IntegerField());
        fields.put("worksheetTable", worksheetTable = new TableField<Integer>());
        fields.put("worksheetAnalyteTable", worksheetAnalyteTable = new TableField<Integer>());       
    }
    
    public WorksheetForm(Node node) {
        this();
        createFields(node);
    }
}
