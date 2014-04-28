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

import static org.openelis.manager.WorksheetManager1Accessor.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.Constants;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.TestAnalyteViewDO;
import org.openelis.domain.TestWorksheetAnalyteViewDO;
import org.openelis.domain.WorksheetAnalysisViewDO;
import org.openelis.domain.WorksheetItemDO;
import org.openelis.domain.WorksheetQcResultViewDO;
import org.openelis.domain.WorksheetResultViewDO;
import org.openelis.domain.WorksheetViewDO;
import org.openelis.manager.TestManager;
import org.openelis.manager.WorksheetManager1;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.NotFoundException;
import org.openelis.utilcommon.ResultFormatter;

@Stateless
@SecurityDomain("openelis")
public class InstrumentInterfaceImportBean {
    @EJB
    LockBean                         lock;
    @EJB
    private SystemVariableBean       systemVariable;
    @EJB
    private TestAnalyteBean          testAnalyte;
    @EJB
    private TestManagerBean          testManager;
    @EJB
    private TestWorksheetAnalyteBean twAnalyte;
    @EJB
    private WorksheetBean            worksheet;
    @EJB
    WorksheetManager1Bean            worksheetManager;

    private static final Logger      log = Logger.getLogger("openelis");

    /*
     * Parse files created by the instrument interface software and load the results
     * into the appropriate worksheet
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void parseFiles() {
        int i;
        BufferedReader bReader;
        HashMap<Integer, String> formatColumnMap;
        HashMap<Integer, HashMap<Integer, String>> formatMap;
        HashMap<String, Integer> fileColumnMap;
        Integer worksheetId, position;
        List<Path> iFiles;
        Path ifDir;
        String ifDirName, fileName, line, parts[], tokens[];
        WorksheetViewDO wVDO;

        try {
            try {
                ifDirName = systemVariable.fetchByName("instrument_file_directory").getValue();
            } catch (NotFoundException nfE) {
                log.severe(Messages.get().instrumentInterface_missingPath());
                return;
            }
            
            ifDir = Paths.get(ifDirName);
            iFiles = getSourceFiles(ifDir);
            formatMap = new HashMap<Integer, HashMap<Integer, String>>();
            for (Path file : iFiles) {
                fileName = file.getFileName().toString();
                parts = fileName.split("-|\\.");
                if (parts.length != 3) {
                    writeErrorFile(file, Messages.get().instrumentInterface_invalidFileName());
                    continue;
                }
    
                try {
                    worksheetId = Integer.parseInt(parts[0]);
                    position = Integer.parseInt(parts[1]);
                } catch (NumberFormatException numE) {
                    writeErrorFile(file, Messages.get().instrumentInterface_invalidFileName());
                    continue;
                }
    
                try {
                    wVDO = worksheet.fetchById(worksheetId);
                } catch (NotFoundException nfE) {
                    writeErrorFile(file, Messages.get().instrumentInterface_worksheetNotFound(worksheetId));
                    continue;
                }
                
                if (!Constants.dictionary().WORKSHEET_WORKING.equals(wVDO.getStatusId())) {
                    writeErrorFile(file, Messages.get().instrumentInterface_worksheetWrongStatus(worksheetId));
                    continue;
                }
                
                formatColumnMap = formatMap.get(wVDO.getFormatId());
                if (formatColumnMap == null) {
                    formatColumnMap = new HashMap<Integer, String>();
                    for (IdNameVO column : worksheetManager.getHeaderLabelsForScreen(wVDO.getFormatId()))
                        formatColumnMap.put(column.getId(), column.getName());
                    formatMap.put(wVDO.getFormatId(), formatColumnMap);
                }
                
                bReader = Files.newBufferedReader(file, Charset.defaultCharset());
                
                i = 0;
                line = bReader.readLine();
                tokens = line.split("\\|");
                fileColumnMap = new HashMap<String, Integer>();
                for (String token : tokens)
                    fileColumnMap.put(token, i++);
                if (fileColumnMap.get("Accession #") == null) {
                    writeErrorFile(file, Messages.get().instrumentInterface_missingRequiredColumn("Accession #"));
                    continue;
                }
                if (fileColumnMap.get("Analyte") == null) { 
                    writeErrorFile(file, Messages.get().instrumentInterface_missingRequiredColumn("Analyte"));
                    continue;
                }
                
                try {
                    importResults(worksheetId, position, bReader, formatColumnMap, fileColumnMap);
                } catch (Exception anyE1) {
                    writeErrorFile(file, anyE1.getMessage());
                    worksheetManager.unlock(worksheetId, WorksheetManager1.Load.NOTE);
                    continue;
                } finally {
                    bReader.close();
                }
                
                Files.delete(file);
            }
        } catch (Exception anyE) {
            log.severe("Instrument Interface Import: "+anyE.getMessage());
            return;
        }
    }

    private List<Path> getSourceFiles(Path dir) throws IOException {
        DirectoryStream<Path> stream;
        HashSet<String> errFiles;
        List<Path> files;
        String name;
        
        files = new ArrayList<Path>();
        errFiles = new HashSet<String>();
        stream = null;
        try {
            stream = Files.newDirectoryStream(dir, "*.err");
            for (Path entry: stream) {
                name = entry.getFileName().toString();
                errFiles.add(name.substring(0, name.length() - 4));
            }
            stream.close();

            stream = Files.newDirectoryStream(dir, "*.csv");
            for (Path entry: stream) {
                name = entry.getFileName().toString();
                if (!errFiles.contains(name.substring(0, name.length() - 4)))
                    files.add(entry);
            }
            stream.close();
        } catch (DirectoryIteratorException ex) {
            if (stream != null)
                stream.close();
            // I/O error encounted during the iteration, the cause is an IOException
            throw ex.getCause();
        }
        return files;
    }
    
    private void writeErrorFile(Path file, String error) {
        PrintWriter writer;
        String name, fullName;
        
        fullName = file.getFileName().toString();
        name = fullName.substring(0,  fullName.length() - 4);
        try {
            writer = new PrintWriter(Files.newOutputStream(file.resolveSibling(Paths.get(name+".err"))));
            writer.println(error);
            writer.flush();
            writer.close();
        } catch (IOException ioE) {
            log.severe("Instrument Interface Import ("+fullName+"): "+error);
            log.severe(ioE.getMessage());
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    private void importResults(Integer worksheetId, Integer position, BufferedReader file,
                               HashMap<Integer, String> formatColumnMap, HashMap<String, Integer> fileColumnMap) throws Exception {
        boolean update;
        int i, j;
        ArrayList<ArrayList<TestAnalyteViewDO>> taList;
        ArrayList<Integer> excludedIds;
        ArrayList<TestAnalyteViewDO> taRow;
        ArrayList<WorksheetResultViewDO> wrVDOs;
        ArrayList<WorksheetQcResultViewDO> wqrVDOs;
        HashMap<Integer, ArrayList<ArrayList<TestAnalyteViewDO>>> testAnalyteMap;
        HashMap<Integer, ArrayList<Integer>> excludedMap;
        HashMap<Integer, HashMap<String, ArrayList<WorksheetResultViewDO>>> wrMapsByAnalysisId;
        HashMap<Integer, HashMap<String, ArrayList<WorksheetQcResultViewDO>>> wqrMapsByAnalysisId;
        HashMap<Integer, ResultFormatter> rfMap;
        HashMap<String, ArrayList<WorksheetResultViewDO>> wrMap;
        HashMap<String, ArrayList<WorksheetQcResultViewDO>> wqrMap;
        HashMap<String, Integer> taColumnMap;
        HashMap<String, WorksheetAnalysisViewDO> waVDOsByAccessionNumber;
        HashSet<Integer> analysisIds;
        Integer colNum;
        ResultFormatter rf;
        SimpleDateFormat format;
        String line, data[], columnName, value;
        TestAnalyteViewDO taVDO;
        TestManager tMan;
        WorksheetAnalysisViewDO waVDO;
        WorksheetManager1 manager;
        WorksheetQcResultViewDO wqrVDO;
        WorksheetResultViewDO wrVDO;
        
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        
        taColumnMap = new HashMap<String, Integer>();
        for (Integer col : formatColumnMap.keySet())
            taColumnMap.put(formatColumnMap.get(col), col);

        update = false;
        manager = worksheetManager.fetchForUpdate(worksheetId);
        excludedMap = new HashMap<Integer, ArrayList<Integer>>();
        rfMap = new HashMap<Integer, ResultFormatter>();
        testAnalyteMap = new HashMap<Integer, ArrayList<ArrayList<TestAnalyteViewDO>>>();
        waVDOsByAccessionNumber = new HashMap<String, WorksheetAnalysisViewDO>();
        wrMapsByAnalysisId = new HashMap<Integer, HashMap<String, ArrayList<WorksheetResultViewDO>>>();
        wqrMapsByAnalysisId = new HashMap<Integer, HashMap<String, ArrayList<WorksheetQcResultViewDO>>>();
        if (getItems(manager) != null) {
            for (WorksheetItemDO item : getItems(manager)) {
                if (position.equals(item.getPosition())) {
                    if (getAnalyses(manager) != null) {
                        analysisIds = new HashSet<Integer>();
                        for (WorksheetAnalysisViewDO ana : getAnalyses(manager)) {
                            if (!item.getId().equals(ana.getWorksheetItemId()))
                                continue;
                            waVDOsByAccessionNumber.put(ana.getAccessionNumber(), ana);
                            analysisIds.add(ana.getId());
                        }
                        if (getResults(manager) != null) {
                            for (WorksheetResultViewDO res : getResults(manager)) {
                                if (!analysisIds.contains(res.getWorksheetAnalysisId()))
                                    continue;
                                wrMap = wrMapsByAnalysisId.get(res.getWorksheetAnalysisId());
                                if (wrMap == null) {
                                    wrMap = new HashMap<String, ArrayList<WorksheetResultViewDO>>();
                                    wrMapsByAnalysisId.put(res.getWorksheetAnalysisId(), wrMap);
                                }
                                wrVDOs = wrMap.get(res.getAnalyteName());
                                if (wrVDOs == null) {
                                    wrVDOs = new ArrayList<WorksheetResultViewDO>();
                                    wrMap.put(res.getAnalyteName(), wrVDOs);
                                }
                                wrVDOs.add(res);
                            }
                        }
                        if (getQcResults(manager) != null) {
                            for (WorksheetQcResultViewDO qcRes : getQcResults(manager)) {
                                if (!analysisIds.contains(qcRes.getWorksheetAnalysisId()))
                                    continue;
                                wqrMap = wqrMapsByAnalysisId.get(qcRes.getWorksheetAnalysisId());
                                if (wqrMap == null) {
                                    wqrMap = new HashMap<String, ArrayList<WorksheetQcResultViewDO>>();
                                    wqrMapsByAnalysisId.put(qcRes.getWorksheetAnalysisId(), wqrMap);
                                }
                                wqrVDOs = wqrMap.get(qcRes.getAnalyteName());
                                if (wqrVDOs == null) {
                                    wqrVDOs = new ArrayList<WorksheetQcResultViewDO>();
                                    wqrMap.put(qcRes.getAnalyteName(), wqrVDOs);
                                }
                                wqrVDOs.add(qcRes);
                            }
                        }
                    } else {
                        worksheetManager.unlock(worksheetId, WorksheetManager1.Load.NOTE);
                        throw new Exception(Messages.get().instrumentInterface_worksheetHasNoAnalysesAtPosition(worksheetId, position));
                    }
                    break;
                }
            }
        } else {
            worksheetManager.unlock(worksheetId, WorksheetManager1.Load.NOTE);
            throw new Exception(Messages.get().instrumentInterface_worksheetHasNoItems(worksheetId));
        }            
        
        while ((line = file.readLine()) != null) {
            data = line.split("\\|");
            waVDO = waVDOsByAccessionNumber.get(data[fileColumnMap.get("Accession #")]);
            if (waVDO != null) {
                if (waVDO.getAnalysisId() != null) {
                    wrMap = wrMapsByAnalysisId.get(waVDO.getId());
                    if (wrMap != null) {
                        wrVDOs = wrMap.get(data[fileColumnMap.get("Analyte")]);
                        if (wrVDOs == null) {
                            taList = testAnalyteMap.get(waVDO.getTestId());
                            excludedIds = excludedMap.get(waVDO.getTestId());
                            if (taList == null) {
                                try {
                                    taList = testAnalyte.fetchByTestId(waVDO.getTestId());
                                    testAnalyteMap.put(waVDO.getTestId(), taList);
                                } catch (Exception anyE) {
                                    throw new Exception("Error loading analyte list for '" +
                                                        waVDO.getTestName() + ", " +
                                                        waVDO.getMethodName() + "' : " +
                                                        anyE.getMessage());
                                }
                            }
                            if (excludedIds == null) {
                                excludedIds = new ArrayList<Integer>();
                                try {
                                    for (TestWorksheetAnalyteViewDO twaVDO : twAnalyte.fetchByTestId(waVDO.getTestId()))
                                        excludedIds.add(twaVDO.getTestAnalyteId());
                                } catch (Exception anyE) {
                                    throw new Exception("Error loading excluded analytes for '" +
                                                        waVDO.getTestName() + ", " +
                                                        waVDO.getMethodName() + "' : " +
                                                        anyE.getMessage());
                                }
                                excludedMap.put(waVDO.getTestId(), excludedIds);
                            }
                            for (i = 0; i < taList.size(); i++) {
                                taRow = taList.get(i);
                                taVDO = taRow.get(0);
                                if (taVDO.getAnalyteName().equals(data[fileColumnMap.get("Analyte")]) &&
                                    (excludedIds.size() == 0 || !excludedIds.contains(taVDO.getId()))) {
                                    wrVDOs = new ArrayList<WorksheetResultViewDO>();
                                    wrVDO = new WorksheetResultViewDO();
                                    wrVDO.setId(manager.getNextUID());
                                    wrVDO.setWorksheetAnalysisId(waVDO.getId());
                                    wrVDO.setTestAnalyteId(taVDO.getId());
                                    wrVDO.setResultRow(i);
                                    wrVDO.setAnalyteId(taVDO.getAnalyteId());
                                    wrVDO.setAnalyteName(taVDO.getAnalyteName());
                                    addResult(manager, wrVDO);
                                    wrVDOs.add(wrVDO);
                                    for (j = 1; j < taRow.size(); j++) {
                                        taVDO = taRow.get(j);
                                        colNum = taColumnMap.get(taVDO.getAnalyteName());
                                        if (colNum != null) {
                                            rf = rfMap.get(taVDO.getTestId());
                                            if (rf == null) {
                                                try {
                                                    tMan = testManager.fetchWithAnalytesAndResults(taVDO.getTestId());
                                                    rfMap.put(taVDO.getTestId(), tMan.getFormatter());
                                                } catch (Exception anyE) {
                                                    throw new Exception("Error loading result formatter for '" +
                                                                        waVDO.getTestName() + ", " +
                                                                        waVDO.getMethodName() + "' : " +
                                                                        anyE.getMessage());
                                                }
                                            }
                                            wrVDO.setValueAt(colNum, rf.getDefault(taVDO.getResultGroup(), waVDO.getUnitOfMeasureId()));
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                        if (wrVDOs != null) {
                            wrVDO = wrVDOs.get(0);
                            for (i = 0; i < 30; i++) {
                                columnName = formatColumnMap.get(i);
                                if (columnName == null)
                                    break;
                                if (fileColumnMap.get(columnName) != null) {
                                    value = data[fileColumnMap.get(columnName)];
                                    wrVDO.setValueAt(i, value);
                                    update = true;
                                }
                            }
                            wrVDOs.remove(0);
                            if (wrVDOs.size() == 0)
                                wrMap.remove(data[fileColumnMap.get("Analyte")]);
                        }
                    }
                } else {
                    wqrMap = wqrMapsByAnalysisId.get(waVDO.getId());
                    if (wqrMap != null) {
                        wqrVDOs = wqrMap.get(data[fileColumnMap.get("Analyte")]);
                        if (wqrVDOs != null) {
                            wqrVDO = wqrVDOs.get(0);
                            for (i = 0; i < 30; i++) {
                                columnName = formatColumnMap.get(i);
                                if (columnName == null)
                                    break;
                                if (fileColumnMap.get(columnName) != null) {
                                    value = data[fileColumnMap.get(columnName)];
                                    wqrVDO.setValueAt(i, value);
                                    update = true;
                                }
                            }
                            wqrVDOs.remove(0);
                            if (wqrVDOs.size() == 0)
                                wqrMap.remove(data[fileColumnMap.get("Analyte")]);
                        }
                    }
                }
                
                value = data[fileColumnMap.get("Analyst")];
                if (value != null && value.length() > 0) {
                    value.replaceAll(";", ",");
                    waVDO.setSystemUsers(value);
                }
                
                value = data[fileColumnMap.get("Start of Analysis")];
                if (value != null && value.length() > 0) {
                    waVDO.setStartedDate(new Datetime(Datetime.YEAR,
                                                      Datetime.MINUTE,
                                                      format.parse((String)value)));
                }
                value = data[fileColumnMap.get("Started Date/Time")];
                if (value != null && value.length() > 0) {
                    waVDO.setStartedDate(new Datetime(Datetime.YEAR,
                                                      Datetime.MINUTE,
                                                      format.parse((String)value)));
                }
            }
        }
        
        if (update)
            worksheetManager.update(manager, null);
        else
            worksheetManager.unlock(worksheetId, WorksheetManager1.Load.NOTE);
    }
}