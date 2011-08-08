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
 * The class is used to carry fields for the web based Final Report for Private wells domain. The fields are considered read/display
 * and do not get committed to the database.
 */

public class SamplePrivateWellFinalReportWebVO extends SampleFinalReportWebVO {

    private static final long serialVersionUID = 1L;

    protected String          privateWellOwner, locationAddressCity;

    public SamplePrivateWellFinalReportWebVO() {
    }

    public SamplePrivateWellFinalReportWebVO(Integer id, Integer accessionNumber, Integer organizationId,
                                    Date collectionDate, Date collectionTime, String location,
                                    String collector, Integer statusId, String locationAddressCity,
                                    String privateWellOwner) {
        super(id, accessionNumber, organizationId, collectionDate, collectionTime, location, collector, statusId);
        setLocationAddressCity(locationAddressCity);
        setPrivateWellOwner(privateWellOwner);
    }
    
    public SamplePrivateWellFinalReportWebVO(Integer id, Integer accessionNumber,
                                             Date collectionDate, Date collectionTime,
                                             Integer statusId, String location,
                                             String privateWellOwner, String collector,
                                             String locationAddressCity, Integer organizationId) {
        super(id, accessionNumber, organizationId, collectionDate, collectionTime, location,
              collector, statusId);
        setPrivateWellOwner(privateWellOwner);
        setLocationAddressCity(locationAddressCity);
    }
             
    public String getPrivateWellOwner() {
        return privateWellOwner;
    }

    public void setPrivateWellOwner(String privateWellOwner) {
        this.privateWellOwner = DataBaseUtil.trim(privateWellOwner);
    }
    
    public String getLocationAddressCity() {
        return locationAddressCity;
    }

    public void setLocationAddressCity(String locationAddressCity) {
        this.locationAddressCity = DataBaseUtil.trim(locationAddressCity);
    }
}
