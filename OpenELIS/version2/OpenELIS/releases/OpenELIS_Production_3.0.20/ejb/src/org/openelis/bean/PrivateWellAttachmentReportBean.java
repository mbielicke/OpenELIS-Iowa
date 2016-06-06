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

import static org.openelis.manager.SampleManager1Accessor.*;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.Constants;
import org.openelis.domain.IdAccessionVO;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.SampleManager1.PostProcessing;
import org.openelis.meta.SampleMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.QueryData;

@Stateless
@SecurityDomain("openelis")
@TransactionManagement(TransactionManagementType.BEAN)
public class PrivateWellAttachmentReportBean {

    @EJB
    private SessionCacheBean    session;

    @EJB
    private SampleBean          sample;

    @EJB
    private SampleManager1Bean  sampleManager1;

    private static final Logger log = Logger.getLogger("openelis");

    public ReportStatus runReport(String accessions) throws Exception {
        int updated;
        SampleManager1 sm;
        ReportStatus status;
        ArrayList<IdAccessionVO> samples;

        status = new ReportStatus();
        session.setAttribute("PrivateWellAttachment", status);

        /*
         * fetch the private well samples for the passed accession numbers
         */
        samples = sample.query(getFields(accessions), 0, -1);

        status.setMessage(Messages.get().report_outputReport()).setPercentComplete(0);

        /*
         * lock and update each sample to generate the attachment for final
         * report
         */
        updated = 0;
        for (IdAccessionVO data : samples) {
            try {
                sm = sampleManager1.fetchForUpdate(data.getId());
                setPostProcessing(sm, PostProcessing.UNRELEASE);
            } catch (Exception e) {
                log.log(Level.SEVERE, "Failed to lock accession number: " +
                                      data.getAccessionNumber(), e);
                continue;
            }
            try {
                sampleManager1.update(sm, true);
                updated++;
            } catch (Exception e) {
                log.log(Level.SEVERE, "Failed to update accession number: " +
                                      data.getAccessionNumber(), e);
                try {
                    sampleManager1.unlock(data.getId());
                } catch (Exception e1) {
                    log.log(Level.SEVERE, "Failed to unlock accession number: " +
                                    data.getAccessionNumber(), e1);
                }
            }
        }

        status.setPercentComplete(100).setMessage("Updated "+ updated+ " out of "+ samples.size()+ " samples");
        return status;
    }

    private ArrayList<QueryData> getFields(String accessions) {
        QueryData field;
        ArrayList<QueryData> fields;

        fields = new ArrayList<QueryData>();

        /*
         * create fields for private well samples in released status
         */
        field = new QueryData();
        field.setKey(SampleMeta.getDomain());
        field.setQuery(Constants.domain().PRIVATEWELL);
        field.setType(QueryData.Type.STRING);
        fields.add(field);

        field = new QueryData();
        field.setKey(SampleMeta.getStatusId());
        field.setQuery(Constants.dictionary().SAMPLE_RELEASED.toString());
        field.setType(QueryData.Type.INTEGER);
        fields.add(field);

        if ( !DataBaseUtil.isEmpty(accessions)) {
            field = new QueryData();
            field.setKey(SampleMeta.getAccessionNumber());
            field.setQuery(accessions);
            field.setType(QueryData.Type.INTEGER);
            fields.add(field);
        }

        return fields;
    }
}