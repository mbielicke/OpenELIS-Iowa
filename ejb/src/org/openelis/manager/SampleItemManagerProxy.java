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
package org.openelis.manager;

import java.util.ArrayList;
import java.util.HashMap;

import org.openelis.domain.DictionaryDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.SampleItemLocal;
import org.openelis.manager.SampleItemManager.SampleItemListItem;
import org.openelis.utils.EJBFactory;

public class SampleItemManagerProxy {
    public SampleItemManager fetchBySampleId(Integer sampleId) throws Exception {
        ArrayList<SampleItemViewDO> list;
        SampleItemManager sim;

        list = EJBFactory.getSampleItem().fetchBySampleId(sampleId);
        sim = SampleItemManager.getInstance();

        sim.addSampleItems(list);
        sim.setSampleId(sampleId);

        return sim;
    }

    public SampleItemManager add(SampleItemManager man) throws Exception {
        Integer sampleItemRefTableId;
        SampleItemViewDO data;
        SampleItemListItem item;
        SampleItemLocal l;

        sampleItemRefTableId = ReferenceTable.SAMPLE_ITEM;

        l = EJBFactory.getSampleItem();
        for (int i = 0; i < man.count(); i++ ) {
            data = man.getSampleItemAt(i);
            data.setSampleId(man.getSampleId());
            l.add(data);
            item = man.getItemAt(i);

            if (item.storage != null) {
                man.getStorageAt(i).setReferenceId(data.getId());
                man.getStorageAt(i).setReferenceTableId(sampleItemRefTableId);
                man.getStorageAt(i).add();
            }

            if (item.analysis != null)
                man.getAnalysisAt(i).setSampleItemId(data.getId());
        }

        addAnalyses(man);

        return man;
    }

    public SampleItemManager update(SampleItemManager man) throws Exception {
        int i;
        Integer sampleItemRefTableId;
        SampleItemLocal l;
        SampleItemViewDO data;
        SampleItemListItem item;

        l = EJBFactory.getSampleItem();
        sampleItemRefTableId = ReferenceTable.SAMPLE_ITEM;

        for (i = 0; i < man.deleteCount(); i++ )
            l.delete(man.getDeletedAt(i).sampleItem);

        for (i = 0; i < man.count(); i++ ) {
            data = man.getSampleItemAt(i);

            if (data.getId() == null) {
                data.setSampleId(man.getSampleId());
                l.add(data);
            } else
                l.update(data);

            item = man.getItemAt(i);
            if (item.storage != null) {
                man.getStorageAt(i).setReferenceId(data.getId());
                man.getStorageAt(i).setReferenceTableId(sampleItemRefTableId);
                man.getStorageAt(i).update();
            }

            if (item.analysis != null)
                man.getAnalysisAt(i).setSampleItemId(data.getId());
        }

        updateAnalyses(man);

        return man;
    }

    public Integer getIdFromSystemName(String systemName) throws Exception {
        DictionaryDO data;

        data = EJBFactory.getDictionary().fetchBySystemName(systemName);
        return data.getId();
    }

    public void validate(SampleItemManager man, ValidationErrorsList errorsList) throws Exception {
        String sequenceNum;
        SampleItemListItem item;
        // you have to have at least 1 sample item
        if (man.count() == 0)
            errorsList.add(new FormErrorException("minOneSampleItemException"));

        for (int i = 0; i < man.count(); i++ ) {
            sequenceNum = man.getSampleItemAt(i).getItemSequence().toString();
            // validate the sample item
            if (man.getSampleItemAt(i).getTypeOfSampleId() == null)
                errorsList.add(new FormErrorException("sampleItemTypeMissing", sequenceNum));

            item = man.getItemAt(i);
            // validate the children
            if (item.storage != null)
                man.getStorageAt(i).validate(errorsList);

            if (item.analysis != null)
                man.getAnalysisAt(i).validate(sequenceNum,
                                              man.getSampleItemAt(i).getTypeOfSampleId(),
                                              man.getSampleManager().getSample().getDomain(),
                                              errorsList);
        }
    }

    private void addAnalyses(SampleItemManager man) throws Exception {
        HashMap<Integer, Integer> idHash;
        int numOfUnresolved;

        idHash = new HashMap<Integer, Integer>();
        numOfUnresolved = 0;
        do {
            for (int i = 0; i < man.count(); i++ )
                numOfUnresolved = numOfUnresolved + man.getAnalysisAt(i).add(idHash);

        } while (numOfUnresolved != 0);
    }

    private void updateAnalyses(SampleItemManager man) throws Exception {
        HashMap<Integer, Integer> idHash;
        int numOfUnresolved;

        idHash = new HashMap<Integer, Integer>();
        numOfUnresolved = 0;
        do {
            for (int i = 0; i < man.count(); i++ )
                numOfUnresolved = numOfUnresolved + man.getAnalysisAt(i).update(idHash);

        } while (numOfUnresolved != 0);
    }

    protected Datetime getCurrentDatetime(byte begin, byte end) throws Exception {
        return Datetime.getInstance(begin, end);
    }
}
