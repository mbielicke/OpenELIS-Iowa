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
 * The class is used to carry fields for the web based Final Report for SDWIS domain. The fields are considered read/display
 * and do not get committed to the database.
 */

public class SampleSDWISFinalReportWebVO extends SampleFinalReportWebVO {

    private static final long serialVersionUID = 1L;

    protected String          pwsId, facilityId, pwsName;

    public SampleSDWISFinalReportWebVO() {
    }    
    
    public SampleSDWISFinalReportWebVO(Integer id, Integer accessionNumber, Integer organizationId, Integer statusId, 
                                  Date collectionDate, Date collectionTime, String location,
                                  String collector, String pwsId, String pwsName,
                                  String facilityId) {
      super(id, accessionNumber, organizationId, collectionDate, collectionTime, location, collector, statusId);
      setPWSId(pwsId);
      setPWSName(pwsName);
      setFacilityId(facilityId);
    }
    
    public SampleSDWISFinalReportWebVO(Integer id, Integer accessionNumber, Date collectionDate, Date collectionTime, Integer statusId, 
                                       String pwsId, String facilityId,
                                       String location, String collector, 
                                       String pwsName, Integer organizationId) {
        super(id, accessionNumber, collectionDate, collectionTime, statusId, collector, location, organizationId);
        setPWSId(pwsId);
        setFacilityId(facilityId);
        setPWSName(pwsName);
    }

    public String getPWSId() {
        return pwsId;
    }

    public void setPWSId(String pwsId) {
        this.pwsId = DataBaseUtil.trim(pwsId);
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = DataBaseUtil.trim(facilityId);
    }
    
    public String getPWSName() {
        return pwsName;
    }

    public void setPWSName(String pwsName) {
        this.pwsName = DataBaseUtil.trim(pwsName);
    }
}
