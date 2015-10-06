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
package org.openelis.bean;

import java.util.ArrayList;
import java.util.HashSet;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.NoteViewDO;
import org.openelis.domain.PatientDO;
import org.openelis.domain.SampleClinicalViewDO;
import org.openelis.domain.SampleNeonatalViewDO;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.SampleManager1Accessor;
import org.openelis.ui.common.DatabaseException;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.common.InconsistencyException;

@Stateless
@SecurityDomain("openelis")
public class PatientMergeHelperBean {

    @EJB
    private PatientBean        patient;
    @EJB
    private SampleClinicalBean sampleClinical;
    @EJB
    private SampleManager1Bean sampleManager1;
    @EJB
    private SampleNeonatalBean sampleNeonatal;
    @EJB
    private UserCacheBean      userCache;

    public void mergePatients(ArrayList<PatientDO> fromPatients, PatientDO toPatient) throws Exception {
        ArrayList<Integer> fromIds;
        ArrayList<SampleClinicalViewDO> scVDOs;
        ArrayList<SampleManager1> sMans;
        ArrayList<SampleNeonatalViewDO> snVDOs;
        HashSet<Integer> sampleIds;
        NoteViewDO mergeNote;
        SampleClinicalViewDO scVDO;
        SampleNeonatalViewDO snVDO;
        
        if (fromPatients == null || fromPatients.size() < 1 || toPatient == null)
            throw new FormErrorException(Messages.get().patientMerge_missingPatient());
        
        fromIds = new ArrayList<Integer>();
        for (PatientDO patDO : fromPatients)
            fromIds.add(patDO.getId());
        
        if (fromIds.size() > 0 && fromIds.contains(toPatient.getId()))
            throw new InconsistencyException(Messages.get().patientMerge_duplicateFromTo());
        
        try {
            fromPatients = patient.fetchForUpdate(fromIds);
            toPatient = patient.fetchForUpdate(toPatient.getId());
            
            //
            // Patients are connected to samples in three places: sample_clinical.patient_id,
            // sample_neonatal.patient_id, and sample_neonatal.next_of_kin_id.
            //
            sampleIds = new HashSet<Integer>();
            scVDOs = sampleClinical.fetchByPatientIds(fromIds);
            if (scVDOs != null && scVDOs.size() > 0) {
                for (SampleClinicalViewDO data : scVDOs)
                    sampleIds.add(data.getSampleId());
            }
            snVDOs = sampleNeonatal.fetchByPatientIds(fromIds);
            if (snVDOs != null && snVDOs.size() > 0) {
                for (SampleNeonatalViewDO data : snVDOs)
                    sampleIds.add(data.getSampleId());
            }
            snVDOs = sampleNeonatal.fetchByNextOfKinIds(fromIds);
            if (snVDOs != null && snVDOs.size() > 0) {
                for (SampleNeonatalViewDO data : snVDOs)
                    sampleIds.add(data.getSampleId());
            }
            
            if (sampleIds.size() > 0) {
                sMans = sampleManager1.fetchForUpdate(new ArrayList<Integer>(sampleIds), SampleManager1.Load.NOTE);
                for (SampleManager1 sm : sMans) {
                    mergeNote = new NoteViewDO();
                    mergeNote.setId(sm.getNextUID());
                    mergeNote.setIsExternal("N");
                    mergeNote.setSubject(Messages.get().patientMerge());
                    mergeNote.setSystemUser(userCache.getPermission().getLoginName());
                    mergeNote.setSystemUserId(userCache.getPermission().getSystemUserId());
                    mergeNote.setTimestamp(Datetime.getInstance(Datetime.YEAR, Datetime.SECOND));
                    SampleManager1Accessor.addSampleInternalNote(sm, mergeNote);

                    if (SampleManager1Accessor.getSampleClinical(sm) != null) {
                        scVDO = SampleManager1Accessor.getSampleClinical(sm);
                        if (fromIds.contains(scVDO.getPatientId())) {
                            addNoteLine(mergeNote, Messages.get().patientMerge_patientNote(scVDO.getPatientId(),
                                                                                           toPatient.getId()));
                            scVDO.setPatientId(toPatient.getId());
                            scVDO.setPatient(toPatient);
                        }
                    } else if (SampleManager1Accessor.getSampleNeonatal(sm) != null) {
                        snVDO = SampleManager1Accessor.getSampleNeonatal(sm);
                        if (fromIds.contains(snVDO.getPatientId())) {
                            addNoteLine(mergeNote, Messages.get().patientMerge_patientNote(snVDO.getPatientId(),
                                                                                           toPatient.getId()));
                            snVDO.setPatientId(toPatient.getId());
                            snVDO.setPatient(toPatient);
                        }
                        if (fromIds.contains(snVDO.getNextOfKinId())) {
                            addNoteLine(mergeNote, Messages.get().patientMerge_nextOfKinNote(snVDO.getNextOfKinId(),
                                                                                             toPatient.getId()));
                            snVDO.setNextOfKinId(toPatient.getId());
                            snVDO.setNextOfKin(toPatient);
                        }
                    }
                }
                
                sMans = sampleManager1.update(sMans, true);
            }

            for (PatientDO patDO : fromPatients)
                patient.delete(patDO);
        } catch (Exception anyE) {
            throw new DatabaseException(anyE.getMessage());
        }
    }
    
    private void addNoteLine(NoteViewDO mergeNote, String line) {
        String text;
        
        if (line == null || line.length() == 0)
            return;
        
        text = mergeNote.getText();
        if (text == null)
            text = "";
        if (text.length() > 0)
            text += "\n";
        text += line;
        mergeNote.setText(text);
    }
}