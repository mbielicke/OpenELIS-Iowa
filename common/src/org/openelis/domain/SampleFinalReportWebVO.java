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
 * The class is the base class used to carry fields for the web based Final Report for different domain. The fields are considered read/display
 * and do not get committed to the database.
 */

public class SampleFinalReportWebVO implements RPC {

    private static final long serialVersionUID = 1L;

    protected Integer         id, accessionNumber, organizationId, statusId;
    protected Datetime        collectionDate, collectionTime;
    protected String          location, collector;

    public SampleFinalReportWebVO() {
    }

    public SampleFinalReportWebVO(Integer id, Integer accessionNumber, Integer organizationId,
                                    Date collectionDate, Date collectionTime, String location,
                                    String collector, Integer statusId) {
        setId(id);
        setAccessionNumber(accessionNumber);
        setOrganizationId(organizationId);
        setCollectionDate(DataBaseUtil.toYD(collectionDate));
        setCollectionTime(DataBaseUtil.toHM(collectionTime));
        setLocation(location);
        setCollector(collector);
        setStatus(statusId);
    }
    
    public SampleFinalReportWebVO(Integer id, Integer accessionNumber, 
                                  Date collectionDate, Date collectionTime, Integer statusId, 
                                  String collector, String location, Integer organizationId) {
      setId(id);
      setAccessionNumber(accessionNumber);
      setCollectionDate(DataBaseUtil.toYD(collectionDate));
      setCollectionTime(DataBaseUtil.toHM(collectionTime));
      setStatus(statusId);
      setLocation(location);
      setCollector(collector);
      setOrganizationId(organizationId);
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

    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }

    public Datetime getCollectionDate() {
        return collectionDate;
    }

    public void setCollectionDate(Datetime collectionDate) {
        this.collectionDate = DataBaseUtil.toYD(collectionDate);
    }

    public Datetime getCollectionTime() {
        return collectionTime;
    }

    public void setCollectionTime(Datetime collectionTime) {
        this.collectionTime = DataBaseUtil.toHM(collectionTime);
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

    public Integer getStatus() {
        return statusId;
    }

    public void setStatus(Integer statusId) {
        this.statusId = statusId;
    }    
}
