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
package org.openelis.meta;

import java.util.Arrays;
import java.util.HashSet;

import org.openelis.gwt.common.Meta;
import org.openelis.gwt.common.MetaMap;

public class PwsMeta implements Meta, MetaMap {

    private static final String ID = "_pws.id",
                                TINSWYS_IS_NUMBER = "_pws.tinwsysIsNumber",
                                NUMBER0 = "_pws.number0",
                                ALTERNATE_ST_NUM = "_pws.alternateStNum",
                                NAME = "_pws.name",
                                ACTIVITY_STATUS_CD = "_pws.activityStatusCd",
                                D_PRIN_CITY_SVD_NM = "_pws.dPrinCitySvdNm",
                                D_PRIN_CNTY_SVD_NM = "_pws.dPrinCntySvdNm",
                                D_POPULATION_COUNT = "_pws.dPopulationCount",
                                D_PWS_ST_TYPE_CD = "_pws.dPwsStTypeCd",
                                ACTIVITY_RSN_TXT = "_pws.activityRsnTxt", 
                                START_DAY = "_pws.startDay",
                                START_MONTH = "_pws.startMonth",
                                END_DAY = "_pws.endDay",
                                END_MONTH = "_pws.endMonth",
                                EFF_BEGIN_DT = "_pws.effBeginDt",
                                EFF_END_DT = "_pws.effEndDt",
                                
                                FAC_ID = "_pwsFacility.id",
                                FAC_TINSWYS_IS_NUMBER = "_pwsFacility.tinwsysIsNumber",
                                FAC_NAME = "_pwsFacility.name",
                                FAC_TYPE_CODE = "_pwsFacility.typeCode",
                                FAC_ST_ASGN_IDENT_CD = "_pwsFacility.stAsgnIdentCd",
                                FAC_ACTIVITY_STATUS_CD = "_pwsFacility.activityStatusCd",
                                FAC_WATER_TYPE_CODE = "_pwsFacility.waterTypeCode",
                                FAC_AVAILABILITY_CODE = "_pwsFacility.availabilityCode",
                                FAC_IDENTIFICATION_CD = "_pwsFacility.identificationCd",
                                FAC_DESCRIPTION_TEXT = "_pwsFacility.descriptionText",
                                FAC_SOURCE_TYPE_CODE = "_pwsFacility.sourceTypeCode",

                                ADDR_ID = "_pwsAddress.id",
                                ADDR_TINSWYS_IS_NUMBER = "_pwsAddress.tinwsysIsNumber",
                                ADDR_TYPE_CODE = "_pwsAddress.typeCode",
                                ADDR_ACTIVE_IND_CD = "_pwsAddress.activeIndCd",
                                ADDR_NAME = "_pwsAddress.name",
                                ADDR_ADDRESS_LINE_ONE_TEXT = "_pwsAddress.addrLineOneTxt",
                                ADDR_ADDRESS_LINE_TWO_TEXT = "_pwsAddress.addrLineTwoTxt",
                                ADDR_ADDRESS_CITY_NAME = "_pwsAddress.addressCityName",
                                ADDR_ADDRESS_STATE_CODE = "_pwsAddress.addressStateCode",
                                ADDR_ADDRESS_ZIP_CODE = "_pwsAddress.addressZipCode",
                                ADDR_STATE_FIPS_CODE = "_pwsAddress.stateFipsCode",
                                ADDR_PHONE_NUMBER = "_pwsAddress.phoneNumber",                 
                                
                                MON_ID = "_pwsMonitor.id",
                                MON_TINSWYS_IS_NUMBER = "_pwsMonitor.tinwsysIsNumber",
                                MON_ST_ASGN_IDENT_CD = "_pwsMonitor.stAsgnIdentCd", 
                                MON_NAME = "_pwsMonitor.name", 
                                MON_TIAANLGP_TIAANLYT_NAME = "_pwsMonitor.tiaanlgpTiaanlytName", 
                                MON_NUMBER_SAMPLES = "_pwsMonitor.numberSamples",
                                MON_COMP_BEGIN_DATE =  "_pwsMonitor.compBeginDate",
                                MON_COMP_END_DATE = "_pwsMonitor.compEndDate",
                                MON_FREQUENCY_NAME = "_pwsMonitor.frequencyName",
                                MON_PERIOD_NAME  = "_pwsMonitor.periodName";
    
    
    private static HashSet<String> names;
    
    static {
        names = new HashSet<String>(Arrays.asList(ID, TINSWYS_IS_NUMBER, NUMBER0, ALTERNATE_ST_NUM, NAME,
                                                  ACTIVITY_STATUS_CD, D_PRIN_CITY_SVD_NM, D_PRIN_CNTY_SVD_NM,
                                                  D_POPULATION_COUNT, D_PWS_ST_TYPE_CD, ACTIVITY_RSN_TXT, START_DAY,
                                                  START_MONTH, END_DAY, END_MONTH, EFF_BEGIN_DT, EFF_END_DT,
                                                  ADDR_ID, ADDR_TINSWYS_IS_NUMBER, ADDR_TYPE_CODE, ADDR_ACTIVE_IND_CD,
                                                  ADDR_NAME, ADDR_ADDRESS_LINE_ONE_TEXT, ADDR_ADDRESS_LINE_TWO_TEXT,
                                                  ADDR_ADDRESS_CITY_NAME, ADDR_ADDRESS_STATE_CODE, ADDR_ADDRESS_ZIP_CODE, 
                                                  ADDR_STATE_FIPS_CODE, ADDR_PHONE_NUMBER, FAC_ID,
                                                  FAC_TINSWYS_IS_NUMBER, FAC_NAME, FAC_TYPE_CODE,
                                                  FAC_ST_ASGN_IDENT_CD, FAC_ACTIVITY_STATUS_CD, FAC_WATER_TYPE_CODE,
                                                  FAC_AVAILABILITY_CODE, FAC_IDENTIFICATION_CD, 
                                                  FAC_DESCRIPTION_TEXT, FAC_SOURCE_TYPE_CODE, MON_ID, 
                                                  MON_TINSWYS_IS_NUMBER, MON_ST_ASGN_IDENT_CD, MON_NAME,
                                                  MON_TIAANLGP_TIAANLYT_NAME, MON_NUMBER_SAMPLES, 
                                                  MON_COMP_BEGIN_DATE, MON_COMP_END_DATE,
                                                  MON_FREQUENCY_NAME, MON_PERIOD_NAME));

    }

    public static String getId() {
        return ID;
    }
    
    public static String getTinwsysIsNumber() {
        return TINSWYS_IS_NUMBER;
    }
    
    public static String getNumber0() {
        return NUMBER0;
    }
    
    public static String getAlternateStNum() {
        return ALTERNATE_ST_NUM;
    }
    
    public static String getName() {
        return NAME;
    }
    
    public static String getActivityStatusCd() {
        return ACTIVITY_STATUS_CD;
    }
    
    public static String getDPrinCitySvdNm() {
        return D_PRIN_CITY_SVD_NM;
    }
    
    public static String getDPrinCntySvdNm() {
        return D_PRIN_CNTY_SVD_NM;
    }
    
    public static String getDPopulationCount() {
        return D_POPULATION_COUNT;
    }
    
    public static String getDPwsStTypeCd() {
        return D_PWS_ST_TYPE_CD;
    }
    
    public static String getActivityRsnTxt() {
        return ACTIVITY_RSN_TXT;
    }
    
    public static String getStartDay() {
        return START_DAY;
    }
    
    public static String getStartMonth() {
        return START_MONTH;
    }
    
    public static String getEndDay() {
        return END_DAY;
    }
    
    public static String getEndMonth() {
        return END_MONTH;
    }
    
    public static String getEffBeginDt() {
        return EFF_BEGIN_DT;
    }
    
    public static String getEffEndDt() {
        return EFF_END_DT;
    }
    
    public static String getFacilityId() {
        return FAC_ID;
    }
    
    public static String getFacilityTinswysIsNumber() {
        return FAC_TINSWYS_IS_NUMBER;
    }
    
    public static String getFacilityName() {
        return FAC_NAME;
    }
    
    public static String getFacilityTypeCode() {
        return FAC_TYPE_CODE;
    }
    
    public static String getFacilityStAsgnIdentCd() {
        return FAC_ST_ASGN_IDENT_CD;
    }
    
    public static String getFacilityActivityStatusCd() {
        return FAC_ACTIVITY_STATUS_CD;
    }
    
    public static String getFacilityWaterTypeCode() {
        return FAC_WATER_TYPE_CODE;
    }
    
    public static String getFacilityAvailabilityCode() {
        return FAC_AVAILABILITY_CODE;
    }
    
    public static String getFacilityIdentificationCd() {
        return FAC_IDENTIFICATION_CD;
    }
    
    public static String getFacilityDescriptionText() {
        return FAC_DESCRIPTION_TEXT;
    }
    
    public static String getFacilitySourceTypeCode() {
        return FAC_SOURCE_TYPE_CODE;
    }
    
    public static String getAddressId() {
        return ADDR_ID;
    }
    
    public static String getAddressTinswysIsNumber() {
        return ADDR_TINSWYS_IS_NUMBER;
    }
    
    public static String getAddressTypeCode() {
        return ADDR_TYPE_CODE;
    }

    public static String getAddressActiveIndCd() {
        return ADDR_ACTIVE_IND_CD;
    }
    
    public static String getAddressName() {
        return ADDR_NAME;
    }
    
    public static String getAddressAddressLineOneText() {
        return ADDR_ADDRESS_LINE_ONE_TEXT;
    }
    
    public static String getAddressAddressLineTwoText() {
        return ADDR_ADDRESS_LINE_TWO_TEXT;
    }
    
    public static String getAddressAddressCityName() {
        return ADDR_ADDRESS_CITY_NAME;
    }
    
    public static String getAddressAddressStateCode() {
        return ADDR_ADDRESS_STATE_CODE;
    }
    
    public static String getAddressAddressZipCode() {
        return ADDR_ADDRESS_ZIP_CODE;
    }
    
    public static String getAddressStateFipsCode() {
        return ADDR_STATE_FIPS_CODE;
    }
    
    public static String getAddressPhoneNumber() {
        return ADDR_PHONE_NUMBER;
    }
    
    public static String getMonitorId() {
        return MON_ID;
    }
    
    public static String getMonitorTinswysIsNumber() {
        return MON_TINSWYS_IS_NUMBER;
    }
    
    public static String getMonitorStAsgnIdentCd() {
        return MON_ST_ASGN_IDENT_CD;
    }
    
    public static String getMonitorName() {
        return MON_NAME;
    }
    
    public static String getMonitorTiaanlgpTiaanlytName() {
        return MON_TIAANLGP_TIAANLYT_NAME;
    }
    
    public static String getMonitorNumberSamples() {
        return MON_NUMBER_SAMPLES;
    }
    
    public static String getMonitorCompBeginDate() {
        return MON_COMP_BEGIN_DATE;
    }
    
    public static String getMonitorCompEndDate() {
        return MON_COMP_END_DATE;
    }
    
    public static String getMonitorFrequencyName() {
        return MON_FREQUENCY_NAME;
    }
    
    public static String getMonitorPeriodName() {
        return MON_PERIOD_NAME;
    }                

    public boolean hasColumn(String columnName) {
        return names.contains(columnName);
    }

    public String buildFrom(String where) {
        String from;
        
        from = "Pws _pws ";
        /*if (where.indexOf("pwsAddress.") > -1)
            from += ",IN (_pws.pwsAddress) _pwsAddress ";
        if (where.indexOf("pwsFacility.") > -1)
            from += ",IN (_pws.pwsFacility) _pwsFacility ";
        if (where.indexOf("pwsMonitor.") > -1)
            from += ",IN (_pws.pwsMonitor) _pwsMonitor ";*/

        return from;
    }

}
