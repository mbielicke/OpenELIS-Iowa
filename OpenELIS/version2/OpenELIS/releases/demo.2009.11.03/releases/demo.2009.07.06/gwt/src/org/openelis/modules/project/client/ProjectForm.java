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
package org.openelis.modules.project.client;

import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.CheckField;
import org.openelis.gwt.common.data.DateField;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.IntegerField;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.gwt.common.data.TableField;
import org.openelis.metamap.ProjectMetaMap;

import com.google.gwt.xml.client.Node;

public class ProjectForm extends Form<Integer> {


    private static final long serialVersionUID = 1L;

    public IntegerField id;
    public StringField name, description,referenceTo;
    public CheckField isActive;
    public DateField completedDate,startedDate;
    public DropDownField<Integer> scripletId,ownerId;
    public TableField<TableDataRow<Integer>> parameterTable;
        
    public ProjectForm() {
        ProjectMetaMap meta = new ProjectMetaMap();
        id = new IntegerField(meta.getId());
        name = new StringField(meta.getName());
        description = new StringField(meta.getDescription());
        referenceTo = new StringField(meta.getReferenceTo());
        isActive = new CheckField(meta.getIsActive());
        completedDate = new DateField(meta.getCompletedDate());
        startedDate = new DateField(meta.getStartedDate());
        scripletId = new DropDownField<Integer>(meta.getScriptlet().getName());
        ownerId = new DropDownField<Integer>(meta.getOwnerId());
        parameterTable = new TableField<TableDataRow<Integer>>("parameterTable");
    }    
    
    public ProjectForm(Node node) {
        this();
        createFields(node);
    }

    public AbstractField[] getFields() {
        return new AbstractField[] {
                                    id,
                                    name,
                                    description,
                                    referenceTo,
                                    isActive,
                                    completedDate,
                                    startedDate,
                                    scripletId,
                                    ownerId,
                                    parameterTable
        };
    }

}
