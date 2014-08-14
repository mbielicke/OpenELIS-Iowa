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
package org.openelis.scriptlet;

import static org.openelis.scriptlet.SampleSO.Operation.*;

import java.util.logging.Level;

import org.openelis.domain.NoteViewDO;
import org.openelis.domain.StandardNoteDO;
import org.openelis.manager.SampleManager1;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.scriptlet.ScriptletInt;

/**
 * The scriptlet for performing operations for the environmental domain e.g. the
 * one related to adding a default note
 */
public class EnvironmentalIAScriptlet1 implements ScriptletInt<SampleSO> {

    private Proxy                 proxy;

    private static StandardNoteDO defaultNote;

    public EnvironmentalIAScriptlet1(Proxy proxy) throws Exception {
        this.proxy = proxy;

        proxy.log(Level.FINE, "Initializing EnvironmentalIAScriptlet1");
        /*
         * the default note for the domain
         */
        if (defaultNote == null) {
            proxy.log(Level.FINE,
                      "Fetching the note whose name is specified by the system variable 'auto_comment_environmental'");
            try {
                defaultNote = proxy.fetchBySystemVariableName("auto_comment_environmental");
            } catch (NotFoundException nfE) {
                /*
                 * ignore not found exception, as this domain may not have a
                 * default note
                 */
                proxy.log(Level.FINE, "Note not found");
            }
        }
    }

    @Override
    public SampleSO run(SampleSO data) {
        proxy.log(Level.FINE, "In EnvironmentalIAScriptlet1.run");

        /*
         * if a default note was found then add it if it's either an uncommitted
         * sample or was previously a quick-entry sample
         */
        if (defaultNote != null && data.getOperations().contains(NEW_DOMAIN_ADDED))
            addDefaultNote(data.getManager());

        return data;
    }

    /**
     * Adds the default note for this domain to the sample
     */
    private void addDefaultNote(SampleManager1 sm) {
        NoteViewDO note;
        
        proxy.log(Level.FINE, "Adding the default note for this domain to the sample");
        
        note = sm.sampleExternalNote.getEditing();
        note.setIsExternal("Y");
        note.setText(defaultNote.getText());
    }

    public static interface Proxy {
        public StandardNoteDO fetchBySystemVariableName(String name) throws Exception;

        public void log(Level level, String message);
    }
}