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
package org.openelis.modules.qc.client;

import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.CheckField;
import org.openelis.gwt.common.data.DateField;
import org.openelis.gwt.common.data.DoubleField;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.IntegerField;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.gwt.common.data.TableField;
import org.openelis.metamap.QcMetaMap;

import com.google.gwt.xml.client.Node;

public class QCForm extends Form<Integer> {

    private static final long serialVersionUID = 1L;

    public IntegerField id;
    public StringField name,source,lotNumber;
    public DropDownField<Integer> typeId,inventoryItemId,preparedUnitId,preparedById;
    public DateField preparedDate,usableDate,expireDate;
    public DoubleField preparedVolume;
    public CheckField isSingleUse;
    public TableField<TableDataRow<Integer>> qcAnalyteTable;
    
    public QCForm() {
        QcMetaMap meta = new QcMetaMap();
        id = new IntegerField(meta.getId());
        name = new StringField(meta.getName());
        source = new StringField(meta.getSource());
        lotNumber = new StringField(meta.getLotNumber());
        typeId = new DropDownField<Integer>(meta.getTypeId());
        inventoryItemId = new DropDownField<Integer>(meta.getInventoryItemId());
        preparedUnitId = new DropDownField<Integer>(meta.getPreparedUnitId());
        preparedById = new DropDownField<Integer>(meta.getPreparedById());
        preparedDate = new DateField(meta.getPreparedDate());
        usableDate = new DateField(meta.getUsableDate());
        expireDate = new DateField(meta.getExpireDate());        
        preparedVolume = new DoubleField(meta.getPreparedVolume());
        isSingleUse = new CheckField(meta.getIsSingleUse());         
        qcAnalyteTable = new TableField<TableDataRow<Integer>>("qcAnalyteTable");
    } 
    
    public QCForm(Node node) {
        this();
        createFields(node);
    }   
   
    public AbstractField[] getFields() {
        return new AbstractField[] {
                                    id,
                                    name,
                                    source,
                                    lotNumber,
                                    typeId,
                                    inventoryItemId,
                                    preparedUnitId,
                                    preparedById,
                                    preparedDate,
                                    usableDate,
                                    expireDate,
                                    preparedVolume,
                                    isSingleUse,
                                    qcAnalyteTable
        };
    }

}
