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
package org.openelis.modules.storageunit.client;

import com.google.gwt.xml.client.Node;

import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.CheckField;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.IntegerField;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.metamap.StorageUnitMetaMap;

public class StorageUnitForm extends Form<Integer>{
    private static final long serialVersionUID = 1L;

    public IntegerField id;
    public DropDownField<String> category;
    public StringField description;
    public CheckField isSingular;
    
    public TableDataModel<TableDataRow<String>> categories;
    
    public StorageUnitForm() {
       StorageUnitMetaMap meta = new StorageUnitMetaMap();
       id = new IntegerField(meta.getId());
       category = new DropDownField<String>(meta.getCategory());
       description = new StringField(meta.getDescription());
       isSingular = new CheckField(meta.getIsSingular());
   }
   
   public StorageUnitForm(Node node) {
       this();
       createFields(node);
   }
   
   public AbstractField[] getFields() {
       return new AbstractField[] {
                                   id,
                                   category,
                                   description,
                                   isSingular
       };
   }
}