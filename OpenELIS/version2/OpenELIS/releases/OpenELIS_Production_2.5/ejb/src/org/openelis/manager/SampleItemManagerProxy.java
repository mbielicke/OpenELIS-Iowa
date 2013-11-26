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

import org.openelis.bean.SampleItemBean;
import org.openelis.bean.StorageBean;
import org.openelis.constants.Messages;
import org.openelis.domain.Constants;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.manager.SampleItemManager.SampleItemListItem;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.common.ValidationErrorsList;
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
        SampleItemBean l;

        sampleItemRefTableId = Constants.table().SAMPLE_ITEM;

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

        updateAnalyses(man);

        return man;
    }

    public SampleItemManager update(SampleItemManager man) throws Exception {
        int i;
        Integer sampleItemRefTableId;
        SampleItemBean l;
        StorageBean sl;
        SampleItemViewDO data;
        SampleItemListItem item;

        l = EJBFactory.getSampleItem();
        sampleItemRefTableId = Constants.table().SAMPLE_ITEM;

        sl = EJBFactory.getStorage();
        for (i = 0; i < man.deleteCount(); i++ ) {
            data = man.getDeletedAt(i).sampleItem;
            sl.deleteById(data.getId(), Constants.table().SAMPLE_ITEM);
            l.delete(data);
        }

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

    public void validate(SampleItemManager man, ValidationErrorsList errorsList) throws Exception {
        String sequenceNum;
        SampleItemListItem item;
        // you have to have at least 1 sample item
        if (man.count() == 0)
            errorsList.add(new FormErrorException(Messages.get().minOneSampleItemException()));

        for (int i = 0; i < man.count(); i++ ) {
            sequenceNum = man.getSampleItemAt(i).getItemSequence().toString();
            // validate the sample item
            if (man.getSampleItemAt(i).getTypeOfSampleId() == null)
                errorsList.add(new FormErrorException(Messages.get().sampleItemTypeMissing(
                                                      sequenceNum)));

            item = man.getItemAt(i);
            // validate the children
            if (item.storage != null)
                man.getStorageAt(i).validate(errorsList);

            if (item.analysis != null)
                man.getAnalysisAt(i).validate(sequenceNum,
                                              man.getSampleItemAt(i).getTypeOfSampleId(),
                                              man.getSampleManager()
                                                 .getSample()
                                                 .getDomain(),
                                              errorsList);
        }
    }

    private void updateAnalyses(SampleItemManager man) throws Exception {
        int i, lastUnresolved, unresolved;
        HashMap<Integer, Integer> idHash;

        idHash = new HashMap<Integer, Integer>();
        lastUnresolved = 0;
        do {
            unresolved = 0;
            for (i = 0; i < man.count(); i++ )
                unresolved += man.getAnalysisAt(i).update(idHash);

            if (unresolved != 0 && unresolved == lastUnresolved)
                throw new Exception("Cannot resolve ids when analysis is linked to itself");

            lastUnresolved = unresolved;
        } while (unresolved != 0);
    }

    protected Datetime getCurrentDatetime(byte begin, byte end) throws Exception {
        return Datetime.getInstance(begin, end);
    }
}
