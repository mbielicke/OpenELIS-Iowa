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
package org.openelis.domain;

import java.util.Date;

import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.RPC;

/**
 * The class is used to carry fields for the web based Final Report for different
 * domains. The fields are considered read/display and do not get committed to 
 * the database.
 */

public class FinalReportWebVO implements RPC {

    private static final long serialVersionUID = 1L;

    protected Integer         id, accessionNumber, revision, organizationId, statusId;
    protected Datetime        collectionDateTime;
    protected String          location, collector, locationAddressCity, projectName,
                              privateWellOwner, sdwisPwsNumber0, sdwisFacilityId, 
                              sdwisPwsName, domain;

    public FinalReportWebVO() {
    }
    
    public FinalReportWebVO(Integer id, Integer accessionNumber, Integer revision,
                            String domain, Date collectionDateTime, Integer statusId,
                            String location, String collector, String locationAddressCity,
                            Integer organizationId, String projectName, String privateWellOwner,
                            String sdwisPwsNumber0, String sdwisFacilityId, String sdwisPwsName) {
        setId(id);
        setAccessionNumber(accessionNumber);
        setRevision(revision);
        setDomain(domain);
        setCollectionDateTime(DataBaseUtil.toYM(collectionDateTime));
        setStatusId(statusId);
        setLocation(location);
        setCollector(collector);
        setLocationAddressCity(locationAddressCity);
        setOrganizationId(organizationId);
        setProjectName(projectName);
        setPrivateWellOwner(privateWellOwner);
        setSdwisPwsNumber0(sdwisPwsNumber0);
        setSdwisFacilityId(sdwisFacilityId);
        setSdwisPwsName(sdwisPwsName);
    }
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAccessionNumber() {
        return accessionNumber;
    }

    public void setAccessionNumber(Integer accessionNumber) {
        this.accessionNumber = accessionNumber;
    }

    public Integer getRevision() {
        return revision;
    }

    public void setRevision(Integer revision) {
        this.revision = revision;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = DataBaseUtil.trim(domain);
    }

    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }

    public Datetime getCollectionDateTime() {
        return collectionDateTime;
    }

    public void setCollectionDateTime(Datetime collectionDate) {
        this.collectionDateTime = DataBaseUtil.toYM(collectionDate);
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = DataBaseUtil.trim(location);
    }

    public String getCollector() {
        return collector;
    }

    public void setCollector(String collector) {
        this.collector = DataBaseUtil.trim(collector);
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    public String getLocationAddressCity() {
        return locationAddressCity;
    }

    public void setLocationAddressCity(String locationAddressCity) {
        this.locationAddressCity = DataBaseUtil.trim(locationAddressCity);
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = DataBaseUtil.trim(projectName);
    }

    public String getPrivateWellOwner() {
        return privateWellOwner;
    }

    public void setPrivateWellOwner(String privateWellOwner) {
        this.privateWellOwner = DataBaseUtil.trim(privateWellOwner);
    }

    public String getSdwisPwsNumber0() {
        return sdwisPwsNumber0;
    }

    public void setSdwisPwsNumber0(String sdwisPwsNumber0) {
        this.sdwisPwsNumber0 = sdwisPwsNumber0;
    }

    public String getSdwisFacilityId() {
        return sdwisFacilityId;
    }

    public void setSdwisFacilityId(String sdwisFacilityId) {
        this.sdwisFacilityId = DataBaseUtil.trim(sdwisFacilityId);
    }

    public String getSdwisPwsName() {
        return sdwisPwsName;
    }

    public void setSdwisPwsName(String sdwisPwsName) {
        this.sdwisPwsName = DataBaseUtil.trim(sdwisPwsName);
    }  
}