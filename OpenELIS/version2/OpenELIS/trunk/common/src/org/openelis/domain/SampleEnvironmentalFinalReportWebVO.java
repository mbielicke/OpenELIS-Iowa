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

/**
 * The class is used to carry fields for the web based Final Report for Environmental domain. The fields are considered read/display
 * and do not get committed to the database.
 */

public class SampleEnvironmentalFinalReportWebVO extends SampleFinalReportWebVO {

    private static final long serialVersionUID = 1L;

    protected String          locationAddressCity, projectName;

    public SampleEnvironmentalFinalReportWebVO() {
    }

    public SampleEnvironmentalFinalReportWebVO(Integer id, Integer accessionNumber, Integer organizationId,
                                    Date collectionDate, Date collectionTime, String location,
                                    String collector, Integer statusId, String locationAddressCity, String projectName) {
        super(id, accessionNumber, organizationId, collectionDate, collectionTime, location, collector, statusId);
        setLocationAddressCity(locationAddressCity);
        setProjectName(projectName);
    } 

    public SampleEnvironmentalFinalReportWebVO(Integer id, Integer accessionNumber,
                                               Date collectionDate, Date collectionTime,
                                               Integer statusId, String location, String collector,
                                               String locationAddressCity, String projectName,
                                               Integer organizationId) {
        super(id, accessionNumber, collectionDate, collectionTime, statusId, collector, location,
              organizationId);
        setLocationAddressCity(locationAddressCity);
        setProjectName(projectName);
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
}
