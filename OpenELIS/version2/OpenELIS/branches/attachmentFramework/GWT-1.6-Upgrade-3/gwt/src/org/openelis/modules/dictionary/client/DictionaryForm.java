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

package org.openelis.modules.dictionary.client;

import com.google.gwt.xml.client.Node;

import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.IntegerField;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.gwt.common.data.TableField;
import org.openelis.metamap.CategoryMetaMap;


public class DictionaryForm extends Form<Integer> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public IntegerField id;
    public StringField systemName;
    public StringField name;
    public TableField<TableDataRow<Integer>> dictEntTable;
    public DropDownField<Integer> sectionId;
    public StringField description;
    
    public TableDataModel<TableDataRow<Integer>> sections;
    
    public DictionaryForm() {
        CategoryMetaMap meta = new CategoryMetaMap();
        id = new IntegerField(meta.getId());
        systemName = new StringField(meta.getSystemName());
        name = new StringField(meta.getName());
        dictEntTable = new TableField<TableDataRow<Integer>>("dictEntTable");
        sectionId = new DropDownField<Integer>(meta.getSectionId());
        description = new StringField(meta.getDescription());
    }
    
    public DictionaryForm(Node node) {
        this();
        createFields(node);
    }
    
    public AbstractField[] getFields() {
        return new AbstractField[] {
                                    id,
                                    systemName,
                                    name,
                                    dictEntTable,
                                    sectionId,
                                    description
        };
    }
}
