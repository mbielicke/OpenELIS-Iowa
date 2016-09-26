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
package org.openelis.scriptlet.sdwis.ia;

import static org.openelis.scriptlet.SampleSO.Action_Before.*;

import java.util.logging.Level;

import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.NoteViewDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.domain.StandardNoteDO;
import org.openelis.scriptlet.SampleSO;
import org.openelis.scriptlet.SampleSO.Action_After;
import org.openelis.scriptlet.sdwis.ScriptletProxy;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.scriptlet.ScriptletInt;

/**
 * The scriptlet for the sdwis domain. It adds a default note to the sample
 * based on the value of a system variable. This is done when the sample is
 * getting assigned the domain for the first time i.e. on clicking "Add" or
 * loading a quick entry sample on the login screen. It also sets a default
 * sample type to a newly added sample item.
 */
public class Scriptlet implements ScriptletInt<SampleSO> {

    private ScriptletProxy        proxy;

    private static StandardNoteDO defaultNote;

    private static DictionaryDO   drinkingWaterDict;

    public Scriptlet(ScriptletProxy proxy) throws Exception {
        this.proxy = proxy;

        proxy.log(Level.FINE, "Initializing SDWISIAScriptlet1");
        /*
         * the default note for the domain
         */
        if (defaultNote == null) {
            proxy.log(Level.FINE,
                      "Fetching the note whose name is specified by the system variable 'auto_comment_sdwis'");
            try {
                defaultNote = proxy.fetchBySystemVariableName("auto_comment_sdwis");
            } catch (NotFoundException nfE) {
                /*
                 * ignore not found exception, as this domain may not have a
                 * default note
                 */
                proxy.log(Level.FINE, "Note not found");
            }
        }

        /*
         * the dictionary entry for the most common sample type for sdwis domain
         */
        if (drinkingWaterDict == null) {
            proxy.log(Level.FINE, "Getting the dictionary for 'drinking_water'");
            drinkingWaterDict = proxy.getDictionaryBySystemName("drinking_water");
        }
    }

    @Override
    public SampleSO run(SampleSO data) {
        proxy.log(Level.FINE, "In SDWISIAScriptlet1.run");

        /*
         * manager adding a default note to an uncommitted sample or previously
         * a quick-entry sample and setting a default sample type to a newly
         * added sample item
         */
        if (data.getActionBefore().contains(NEW_DOMAIN)) {
            if (defaultNote != null)
                addDefaultNote(data);
        } else if (data.getActionBefore().contains(SAMPLE_ITEM_ADDED)) {
            setSampleType(data);
        }

        return data;
    }

    /**
     * Adds the default note for this domain to the sample
     */
    private void addDefaultNote(SampleSO data) {
        NoteViewDO note;

        note = data.getManager().sampleExternalNote.getEditing();
        /*
         * if the scriptlet was called to add a note to a previously
         * quick-entered sample, the sample can have an external note; this
         * check makes sure that note doesn't get overwritten here
         */
        if (DataBaseUtil.isEmpty(note.getText())) {
            proxy.log(Level.FINE, "Adding the default note for this domain to the sample");
            note.setText(defaultNote.getText());
        }
        data.addChangedUid(Constants.uid().getNote(note.getId()));
    }

    /**
     * Sets the sample type of the sample item whose uid is the passed value, as
     * 'drinking water'
     */
    private void setSampleType(SampleSO data) {
        SampleItemViewDO item;

        item = (SampleItemViewDO)data.getManager().getObject(data.getUid());
        proxy.log(Level.FINE, "Setting the sample type of the sample item with uid:  " +
                              data.getUid() + " as " + drinkingWaterDict.getSystemName());
        item.setTypeOfSampleId(drinkingWaterDict.getId());
        item.setTypeOfSample(drinkingWaterDict.getEntry());
        data.addChangedUid(Constants.uid().getSampleItem(item.getId()));
        data.addActionAfter(Action_After.SAMPLE_ITEM_CHANGED);
    }
}