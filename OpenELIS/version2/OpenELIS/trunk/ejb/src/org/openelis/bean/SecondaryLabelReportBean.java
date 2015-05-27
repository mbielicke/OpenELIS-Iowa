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

import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.PatientDO;
import org.openelis.domain.SecondaryLabelVO;
import org.openelis.domain.TestViewDO;
import org.openelis.manager.SampleManager1;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.InconsistencyException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.utils.ReportUtil;
import org.openelis.utils.User;

@Stateless
@SecurityDomain("openelis")
public class SecondaryLabelReportBean {

    @Resource
    private SessionContext      ctx;

    @EJB
    private SessionCacheBean    session;

    @EJB
    private LabelReportBean     labelReport;

    @EJB
    private SampleManager1Bean  sampleManager1;

    @EJB
    private TestBean            test;

    private static final Logger log = Logger.getLogger("openelis");

    /*
     * Execute the report and send its output to specified location
     */
    public ReportStatus runReport(ArrayList<SecondaryLabelVO> labels) throws Exception {
        Integer testId, accession;
        String printer, printstat;
        SampleManager1 sm;
        TestViewDO t;
        PrintStream ps;
        Path path;
        ReportStatus status;
        ValidationErrorsList e;
        ArrayList<Integer> ids;
        ArrayList<SampleManager1> sms;
        HashMap<Integer, SampleManager1> smap;
        HashMap<Integer, TestViewDO> tests;

        if (labels == null || labels.size() == 0)
            throw new InconsistencyException(Messages.get()
                                                     .secondaryLabel_noLabelsToPrintException());

        status = new ReportStatus();
        session.setAttribute("SecondaryLabelReport", status);

        printer = labels.get(0).getPrinter();
        if (DataBaseUtil.isEmpty(printer))
            throw new InconsistencyException(Messages.get()
                                                     .secondaryLabel_printerUnspecifiedException());

        /*
         * fetch the managers for the samples specified by the user and create a
         * mapping between sample id and manager
         */
        ids = new ArrayList<Integer>();
        for (SecondaryLabelVO data : labels)
            ids.add(data.getSampleId());

        sms = sampleManager1.fetchByIds(ids);
        smap = new HashMap<Integer, SampleManager1>();
        for (SampleManager1 man : sms)
            smap.put(getSample(man).getId(), man);

        status.setMessage(Messages.get().report_outputReport()).setPercentComplete(0);

        /*
         * print the labels and send it to printer
         */
        path = ReportUtil.createTempFile("secondarylabel", ".txt", null);
        ps = new PrintStream(Files.newOutputStream(path));

        status.setPercentComplete(50);
        tests = new HashMap<Integer, TestViewDO>();
        e = new ValidationErrorsList();
        for (SecondaryLabelVO data : labels) {
            sm = smap.get(data.getSampleId());
            if (sm == null) {
                e.add(new NotFoundException(Messages.get()
                                                    .secondaryLabel_sampleNotExistException(data.getSampleId())));
                continue;
            }

            accession = getSample(sm).getAccessionNumber();
            t = null;
            for (AnalysisViewDO ana : getAnalyses(sm)) {
                /*
                 * if a label needs to be printed for this analysis then find
                 * the test it's linked to
                 */
                if (ana.getId().equals(data.getAnalysisId())) {
                    testId = ana.getTestId();
                    t = tests.get(testId);
                    if (t == null) {
                        try {
                            /*
                             * tests are fetched one at a time and not all at
                             * once because labels for at most 2 or 3 tests are
                             * expected to be printed at any given time
                             */
                            t = test.fetchById(testId);
                            tests.put(testId, t);
                        } catch (NotFoundException ex) {
                            e.add(new NotFoundException(Messages.get()
                                                                .secondaryLabel_testNotExistException(accession,
                                                                                                      testId)));
                        } catch (Exception ex) {
                            log.log(Level.SEVERE,
                                    Messages.get()
                                            .secondaryLabel_failedToFetchTestException(accession,
                                                                                       testId),
                                    ex);
                            e.add(ex);
                        }
                    }
                    if (t != null)
                        printLabel(ps, sm, t, data.getLabelQty(), e);
                    break;
                }
            }
        }

        ps.close();

        printstat = ReportUtil.print(path, User.getName(ctx), printer, 1, true);
        /*
         * exceptions are added to a ValidationErrorsList instead of thrown
         * individually so that all labels that don't have any errors can be
         * printed
         */
        if (e.size() > 0)
            throw e;

        status.setPercentComplete(100).setMessage(printstat).setStatus(ReportStatus.Status.PRINTED);

        return status;
    }

    /**
     * Print the label defined for the passed test by obtaining the required
     * data from the manager and the test
     */
    private void printLabel(PrintStream ps, SampleManager1 sm, TestViewDO t, Integer labelQty, ValidationErrorsList e) {
        Integer accession;

        accession = getSample(sm).getAccessionNumber();
        if (t.getLabelId() == null) {
            e.add(new InconsistencyException(Messages.get()
                                                     .secondaryLabel_labelNotDefinedException(accession,
                                                                                              t.getName(),
                                                                                              t.getMethodName())));
            return;
        } else if (labelQty == null || labelQty <= 0) {
            e.add(new InconsistencyException(Messages.get()
                                                     .secondaryLabel_labelQtyLessThanOneException(accession,
                                                                                                  t.getName(),
                                                                                                  t.getMethodName())));
            return;
        }
        /*
         * based on the name of the label, obtain the required information and
         * print the label of a specific format
         */
        switch (t.getLabelName()) {
            case "1x2 acc+rec":
                accessionReceivedLabel(ps, sm, t, labelQty);
                break;
            case "1x2 acc+rec+test":
                accessionReceivedTestLabel(ps, sm, t, labelQty);
                break;
            case "1x2 acc+rec+test+method":
                accessionReceivedTestMethodLabel(ps, sm, t, labelQty);
                break;
            case "1x2 acc+pat+rec+test":
                accessionPatientReceivedTestLabel(ps, sm, t, labelQty, e);
                break;
            default:
                /*
                 * unknown label type
                 */
                e.add(new InconsistencyException(Messages.get()
                                                         .secondaryLabel_unknownLabelTypeException(accession,
                                                                                                   t.getName(),
                                                                                                   t.getMethodName())));
        }
    }

    /**
     * Prints the label showing accession number, patient name, received date
     * and test name
     */
    private void accessionPatientReceivedTestLabel(PrintStream ps, SampleManager1 sm, TestViewDO t,
                                                   Integer labelQty, ValidationErrorsList e) {
        Integer accession;
        String received, patientName;
        PatientDO pat;

        pat = null;
        if (getSampleClinical(sm) != null)
            pat = getSampleClinical(sm).getPatient();
        else if (getSampleNeonatal(sm) != null)
            pat = getSampleNeonatal(sm).getPatient();

        accession = getSample(sm).getAccessionNumber();
        if (pat != null)
            patientName = DataBaseUtil.concatWithSeparator(pat.getLastName(),
                                                           ", ",
                                                           pat.getFirstName());
        else
            patientName = "";

        received = ReportUtil.toString(getSample(sm).getReceivedDate(), Messages.get()
                                                                                .dateTimePattern());

        labelReport.accessionPatientReceivedTestLabel(ps,
                                                      accession,
                                                      patientName,
                                                      received,
                                                      t.getName(),
                                                      labelQty);
    }

    /**
     * Prints the label showing accession number and received date
     */
    private void accessionReceivedLabel(PrintStream ps, SampleManager1 sm, TestViewDO t, Integer labelQty) {
        String received;

        received = ReportUtil.toString(getSample(sm).getReceivedDate(), Messages.get()
                                                                                .dateTimePattern());
        labelReport.accessionReceivedLabel(ps,
                                           getSample(sm).getAccessionNumber(),
                                           received,
                                           labelQty);
    }

    /**
     * Prints the label showing accession number, received date and test name
     */
    private void accessionReceivedTestLabel(PrintStream ps, SampleManager1 sm, TestViewDO t, Integer labelQty) {
        String received;

        received = ReportUtil.toString(getSample(sm).getReceivedDate(), Messages.get()
                                                                                .dateTimePattern());
        labelReport.accessionReceivedTestLabel(ps,
                                               getSample(sm).getAccessionNumber(),
                                               received,
                                               t.getName(),
                                               labelQty);
    }

    /**
     * Prints the label showing accession number, received date and test and
     * method names
     */
    private void accessionReceivedTestMethodLabel(PrintStream ps, SampleManager1 sm, TestViewDO t, Integer labelQty) {
        String received;

        received = ReportUtil.toString(getSample(sm).getReceivedDate(), Messages.get()
                                                                                .dateTimePattern());
        labelReport.accessionReceivedTestMethodLabel(ps,
                                                     getSample(sm).getAccessionNumber(),
                                                     received,
                                                     t.getName(),
                                                     t.getMethodName(),
                                                     labelQty);
    }
}