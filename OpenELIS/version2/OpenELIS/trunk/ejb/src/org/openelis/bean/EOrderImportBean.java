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

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.EOrderBodyDO;
import org.openelis.domain.EOrderDO;
import org.openelis.domain.EOrderLinkDO;
import org.openelis.domain.SampleDO;
import org.openelis.ui.common.DatabaseException;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

@Stateless
@SecurityDomain("openelis")
public class EOrderImportBean {
    @EJB
    EOrderBean                  eorder;
    @EJB
    EOrderBodyBean              eorderBody;
    @EJB
    EOrderLinkBean              eorderLink;
    @EJB
    SampleBean                  sample;
    @EJB
    private SystemVariableBean  systemVariable;

    private static final Logger log = Logger.getLogger("openelis");

    /*
     * Parse files created by the HL7 software and load the results into the
     * eorder tables.
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void parseFiles() {
        List<Path> iFiles;
        Path ifDir;
        String ifDirName;

        try {
            try {
                ifDirName = systemVariable.fetchByName("eorder_file_directory").getValue();
            } catch (NotFoundException nfE) {
                log.severe(Messages.get().eorderImport_missingPath());
                return;
            }

            ifDir = Paths.get(ifDirName);
            iFiles = getSourceFiles(ifDir);
            for (Path file : iFiles) {
                try {
                    importData(file);
                } catch (Exception anyE1) {
                    writeErrorFile(file, anyE1.getMessage());
                    continue;
                }

                Files.delete(file);
            }
        } catch (Exception anyE) {
            log.severe("EOrder Import: " + anyE.getMessage());
            return;
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    private void importData(Path file) throws Exception {
        int i;
        ArrayList<EOrderDO> prevEOrders;
        ArrayList<EOrderLinkDO> eorderLinkDOs, prevEOrderLinkDOs;
        ArrayList<SampleDO> samples;
        Document document, document2;
        EOrderDO eorderDO;
        EOrderBodyDO eorderBodyDO, prevEOrderBodyDO;
        EOrderLinkDO eorderLinkDO;
        Element orderElem, eorderElem, eorderBodyElem, eorderLinkElem, eorderLinkItemElem,
                prevEOrderBodyElem;
        NodeList nodes, nodes2;
        String bodyXml, organizationName, placerOrderNum;

        document = XMLUtil.load(file.toAbsolutePath().toString());
        orderElem = (Element)document.getDocumentElement();
        eorderElem = (Element)orderElem.getElementsByTagName("eorder").item(0);

        eorderDO = new EOrderDO();
        eorderDO.setEnteredDate(Datetime.getInstance(Datetime.YEAR, Datetime.SECOND));
        if (XMLUtil.getNodeText(eorderElem, "paper_order_validator") != null)
            eorderDO.setPaperOrderValidator(XMLUtil.getNodeText(eorderElem, "paper_order_validator").toUpperCase());
        eorderDO.setDescription(XMLUtil.getNodeText(eorderElem, "description"));

        eorderBodyElem = (Element)orderElem.getElementsByTagName("eorder_body").item(0);
        eorderBodyDO = new EOrderBodyDO();
        eorderBodyDO.setEOrderId(eorderDO.getId());
        bodyXml = XMLUtil.toString(eorderBodyElem);
        if (bodyXml != null)
            bodyXml = bodyXml.replaceAll("[^\\n\\r\\t\\p{Print}]", "?");
        eorderBodyDO.setXml(bodyXml);

        eorderLinkElem = (Element)orderElem.getElementsByTagName("eorder_link").item(0);
        nodes = eorderLinkElem.getElementsByTagName("eorder_link_item");
        eorderLinkDOs = new ArrayList<EOrderLinkDO>();
        if (nodes.getLength() > 0) {
            for (i = 0; i < nodes.getLength(); i++ ) {
                eorderLinkItemElem = (Element)nodes.item(i);
                eorderLinkDO = new EOrderLinkDO();
                eorderLinkDO.setEOrderId(eorderDO.getId());
                eorderLinkDO.setReference(XMLUtil.getNodeText(eorderLinkItemElem, "reference"));
                eorderLinkDO.setSubId(XMLUtil.getNodeText(eorderLinkItemElem, "sub_id"));
                eorderLinkDO.setName(XMLUtil.getNodeText(eorderLinkItemElem, "name"));
                eorderLinkDO.setValue(XMLUtil.getNodeText(eorderLinkItemElem, "value"));
                if ("ORC_1".equals(eorderLinkDO.getName().trim()) &&
                    "CA".equals(eorderLinkDO.getValue().trim())) {
                    organizationName = null;
                    nodes2 = eorderBodyElem.getElementsByTagName("organization");
                    if (nodes2.getLength() > 0)
                        organizationName = XMLUtil.getNodeText((Element)nodes2.item(0), "name");

                    placerOrderNum = null;
                    nodes2 = eorderBodyElem.getElementsByTagName("test");
                    if (nodes2.getLength() > 0)
                        placerOrderNum = XMLUtil.getNodeText((Element)nodes2.item(0),
                                                             "placer_order_num");

                    prevEOrders = eorder.fetchByPaperOrderValidator(eorderDO.getPaperOrderValidator());
                    if (prevEOrders != null && prevEOrders.size() > 0) {
                        for (EOrderDO prevEOrderDO : prevEOrders) {
                            try {
                                prevEOrderBodyDO = eorderBody.fetchByEOrderId(prevEOrderDO.getId());
                                document2 = XMLUtil.parse(prevEOrderBodyDO.getXml());
                                prevEOrderBodyElem = (Element)document2.getDocumentElement();
                                nodes2 = prevEOrderBodyElem.getElementsByTagName("organization");
                                if (nodes2.getLength() > 0 &&
                                    organizationName != null &&
                                    organizationName.equals(XMLUtil.getNodeText((Element)nodes2.item(0),
                                                                                "name"))) {
                                    nodes2 = prevEOrderBodyElem.getElementsByTagName("test");
                                    if (nodes2.getLength() > 0 &&
                                        placerOrderNum != null &&
                                        placerOrderNum.equals(XMLUtil.getNodeText((Element)nodes2.item(0),
                                                                                  "placer_order_num"))) {
                                        prevEOrderDO = eorder.fetchForUpdate(prevEOrderDO.getId());
                                        try {
                                            samples = sample.fetchByEOrderId(prevEOrderDO.getId());
                                            if (samples != null && samples.size() > 0) {
                                                prevEOrderDO.setDescription("(Cancelled) " +
                                                                            prevEOrderDO.getDescription());
                                                eorder.update(prevEOrderDO);
                                            } else {
                                                eorderBody.delete(prevEOrderBodyDO);
                                                prevEOrderLinkDOs = eorderLink.fetchByEOrderId(prevEOrderDO.getId());
                                                if (prevEOrderLinkDOs != null &&
                                                    prevEOrderLinkDOs.size() > 0) {
                                                    for (EOrderLinkDO prevEOrderLinkDO : prevEOrderLinkDOs)
                                                        eorderLink.delete(prevEOrderLinkDO);
                                                }
                                                eorder.delete(prevEOrderDO);
                                            }
                                        } catch (Exception anyE1) {
                                            throw anyE1;
                                        }
                                    }
                                }
                            } catch (Exception anyE) {
                                throw new DatabaseException("Error cancelling previous orders for ID " +
                                                            eorderDO.getId() + ": " + anyE.getMessage());
                            }
                        }
                    }
                    return;
                }
                eorderLinkDOs.add(eorderLinkDO);
            }
        }

        try {
            eorder.add(eorderDO);

            eorderBodyDO.setEOrderId(eorderDO.getId());
            eorderBody.add(eorderBodyDO);
            for (i = 0; i < eorderLinkDOs.size(); i++ ) {
                eorderLinkDO = eorderLinkDOs.get(i);
                eorderLinkDO.setEOrderId(eorderDO.getId());
                eorderLink.add(eorderLinkDO);
            }
        } catch (Exception anyE) {
            throw new DatabaseException(anyE);
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
            for (Path entry : stream) {
                name = entry.getFileName().toString();
                errFiles.add(name.substring(0, name.length() - 4));
            }
        } finally {
            try {
                if (stream != null)
                    stream.close();
            } catch (Exception e) {
                log.log(Level.SEVERE, e.getMessage(), e);
            }
        }

        stream = null;
        try {
            stream = Files.newDirectoryStream(dir, "*.xml");
            for (Path entry : stream) {
                name = entry.getFileName().toString();
                if ( !errFiles.contains(name.substring(0, name.length() - 4)))
                    files.add(entry);
            }
        } finally {
            try {
                if (stream != null)
                    stream.close();
            } catch (Exception e) {
                log.log(Level.SEVERE, e.getMessage(), e);
            }
        }
        return files;
    }

    private void writeErrorFile(Path file, String error) {
        PrintWriter writer;
        String name, fullName;

        fullName = file.getFileName().toString();
        name = fullName.substring(0, fullName.length() - 4);
        writer = null;
        try {
            writer = new PrintWriter(Files.newOutputStream(file.resolveSibling(Paths.get(name +
                                                                                         ".err"))));
            writer.println(error);
            writer.flush();
        } catch (IOException ioE) {
            log.severe("EOrder Import (" + fullName + "): " + error);
            log.severe(ioE.getMessage());
        } finally {
            try {
                if (writer != null)
                    writer.close();
            } catch (Exception e) {
                log.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }
}