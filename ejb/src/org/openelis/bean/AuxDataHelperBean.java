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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.AddressDO;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.AuxFieldGroupDO;
import org.openelis.domain.AuxFieldViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.PWSDO;
import org.openelis.domain.ProjectDO;
import org.openelis.domain.SampleDO;
import org.openelis.domain.SampleEnvironmentalDO;
import org.openelis.domain.SamplePrivateWellViewDO;
import org.openelis.domain.SampleProjectViewDO;
import org.openelis.domain.SampleSDWISViewDO;
import org.openelis.domain.SystemVariableDO;
import org.openelis.manager.AuxFieldGroupManager;
import org.openelis.manager.AuxFieldManager;
import org.openelis.manager.SampleManager1;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.FormErrorWarning;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.utilcommon.ResultFormatter;
import org.openelis.utils.ReportUtil;

/**
 * This class is used for adding or removing aux data associated with a sample,
 * order etc.
 */

@Stateless
@SecurityDomain("openelis")
public class AuxDataHelperBean {

    @EJB
    private DictionaryCacheBean      dictionaryCache;

    @EJB
    private CategoryCacheBean        categoryCache;

    @EJB
    private AuxFieldGroupManagerBean auxFieldGroupManager;

    @EJB
    private ProjectBean              project;

    @EJB
    private PWSBean                  pws;

    @EJB
    private SystemVariableBean       systemVariable;

    @EJB
    private AuxFieldGroupBean        auxFieldGroup;

    private static final Logger      log = Logger.getLogger("openelis");

    public static final String       SAMPLE_ENV_AUX_DATA = "sample_env_aux_data",
                    SAMPLE_WELL_AUX_DATA = "sample_well_aux_data",
                    SAMPLE_SDWIS_AUX_DATA = "sample_sdwis_aux_data",
                    SMPL_COLLECTED_DATE = "smpl_collected_date",
                    SMPL_COLLECTED_TIME = "smpl_collected_time",
                    SMPL_CLIENT_REF = "smpl_client_ref", IS_HAZARDOUS = "is_hazardous",
                    COLLECTOR = "collector", LOCATION = "location",
                    LOC_MULT_UNIT = "loc_mult_unit", LOC_STREET_ADDRESS = "loc_street_address",
                    LOC_CITY = "loc_city", LOC_STATE = "loc_state", LOC_ZIP_CODE = "loc_zip_code",
                    LOC_COUNTRY = "loc_country", PRIORITY = "priority",
                    COLLECTOR_PHONE = "collector_phone", DESCRIPTION = "description",
                    PROJECT_NAME = "project_name", OWNER = "owner", WELL_NUMBER = "well_number",
                    PWS_ID = "pws_id", STATE_LAB_NUM = "state_lab_num",
                    FACILITY_ID = "facility_id", SAMPLE_TYPE = "sample_type",
                    SAMPLE_CAT = "sample_cat", SAMPLE_PT_ID = "sample_pt_id", YES = "yes",
                    STATE = "state", COUNTRY = "country", SDWIS_SAMPLE_TYPE = "sdwis_sample_type",
                    SDWIS_SAMPLE_CATEGORY = "sdwis_sample_category",
                    ORG_HOLD_SAMPLE = "org_hold_sample", ORIG_SAMPLE_NUMBER = "orig_sample_number",
                    TEMPERATURE = "temperature", VOLUME = "volume", PRESSURE = "pressure",
                    NULL_DATA_CODE = "null_data_code", FILTER_LOT_BLANK = "filter_lot_blank",
                    STRIPS_PER_FILTER = "strips_per_filter",
                    BLANK_SAMPLE_NUMBER = "blank_sample_number", STATE_CODE = "state_code",
                    COUNTY_CODE = "county_code", SITE_ID = "site_id", POC = "poc",
                    COLLECTION_FREQUENCY = "collection_frequency";

    /**
     * Adds aux groups specified by the list of ids to the list of aux data, if
     * the group isn't already present in the list of aux data.
     */
    public void addAuxGroups(ArrayList<AuxDataViewDO> auxiliary, ArrayList<Integer> groupIds,
                             ValidationErrorsList e) throws Exception {
        addAuxGroups(auxiliary, groupIds, null, e);
    }

    /**
     * Adds aux group specified by the ids to the list of aux data. If an aux
     * data's analyte can be found in the map then the value is set from the
     * map, otherwise it's set as the default of the corresponding aux field in
     * the group.
     */
    public void addAuxGroups(ArrayList<AuxDataViewDO> auxiliary, ArrayList<Integer> groupIds,
                             HashMap<Integer, HashMap<Integer, AuxDataViewDO>> grps,
                             ValidationErrorsList e) throws Exception {
        AuxFieldViewDO af;
        AuxFieldManager afm;
        AuxFieldGroupDO afg;
        AuxFieldGroupManager afgm;
        AuxDataViewDO aux1, aux2;
        ResultFormatter rf;
        ArrayList<Integer> addIds;
        HashMap<Integer, AuxDataViewDO> auxMap;

        /*
         * make sure that only the groups not already in the list of aux data
         * get added to it
         */
        addIds = getDifference(auxiliary, groupIds);

        for (Integer id : addIds) {
            auxMap = null;
            if (grps != null)
                auxMap = grps.get(id);

            /*
             * fields for the aux group are fetched and aux data for them is
             * added
             */
            afgm = auxFieldGroupManager.fetchByIdWithFields(id);
            afg = afgm.getGroup();

            /*
             * the aux group must be active to be added to the manager
             */
            if ("N".equals(afg.getIsActive())) {
                e.add(new FormErrorWarning(Messages.get().aux_inactiveGroupException(afg.getName())));
                continue;
            }

            afm = afgm.getFields();
            rf = afgm.getFormatter();
            for (int i = 0; i < afm.count(); i++ ) {
                af = afm.getAuxFieldAt(i);
                if ("N".equals(af.getIsActive()))
                    continue;
                aux1 = new AuxDataViewDO();
                aux1.setAuxFieldId(af.getId());
                aux1.setAuxFieldGroupId(id);
                aux1.setAuxFieldGroupName(afg.getName());
                aux1.setAnalyteId(af.getAnalyteId());
                aux1.setAnalyteName(af.getAnalyteName());
                aux1.setIsReportable(af.getIsReportable());
                if (auxMap == null) {
                    /*
                     * set the value as the default for the aux data's field
                     */
                    aux1.setValue(rf.getDefault(af.getId(), null));
                } else {
                    /*
                     * set the value from the map if the aux data's analyte can
                     * be found in the map
                     */
                    aux2 = auxMap.get(af.getAnalyteId());
                    if (aux2 != null) {
                        aux1.setIsReportable(aux2.getIsReportable());
                        aux1.setTypeId(aux2.getTypeId());
                        aux1.setValue(aux2.getValue());
                    }
                }
                auxiliary.add(aux1);
            }
        }
    }

    /**
     * Removes the groups specified by the ids from the list of aux data. Adds
     * the removed objects to the list returned.
     */
    public ArrayList<AuxDataViewDO> removeAuxGroups(ArrayList<AuxDataViewDO> auxiliary,
                                                    Set<Integer> groupIds) {
        Integer prevId;
        ArrayList<AuxDataViewDO> removed;

        removed = new ArrayList<AuxDataViewDO>();
        for (int i = 0; i < auxiliary.size(); i++ ) {
            prevId = auxiliary.get(i).getAuxFieldGroupId();
            if (groupIds.contains(prevId)) {
                do {
                    removed.add(auxiliary.remove(i));
                } while (i < auxiliary.size() &&
                         auxiliary.get(i).getAuxFieldGroupId().equals(prevId));
            }
        }

        return removed;
    }

    /**
     * copy auxiliary data from the list to sample data
     */
    public void copyToSample(SampleManager1 sm, ArrayList<AuxDataViewDO> auxiliary,
                             ValidationErrorsList e) throws Exception {
        Integer accession, domainGrpId, prevGrpId;
        SampleDO data;
        ArrayList<Integer> grpIds;
        HashMap<Integer, AuxDataViewDO> auxGrp;
        HashMap<Integer, HashMap<Integer, AuxDataViewDO>> auxGrps;

        if (auxiliary == null)
            return;

        data = getSample(sm);
        /*
         * for display
         */
        accession = getSample(sm).getAccessionNumber();
        if (accession == null)
            accession = 0;
        /*
         * make a hash of aux groups
         */
        domainGrpId = getDomainAuxGroupId(data);
        grpIds = new ArrayList<Integer>();
        auxGrps = new HashMap<Integer, HashMap<Integer, AuxDataViewDO>>();
        prevGrpId = null;
        for (AuxDataViewDO aux : auxiliary) {
            /*
             * consider this aux group only if it's active
             */
            if ("N".equals(aux.getAuxFieldGroupIsActive())) {
                if (!aux.getAuxFieldGroupId().equals(prevGrpId))
                    e.add(new FormErrorWarning(Messages.get().sample_inactiveAuxGroupWarning(accession, aux.getAuxFieldGroupName())));
                prevGrpId = aux.getAuxFieldGroupId();
                continue;
            }
            auxGrp = auxGrps.get(aux.getAuxFieldGroupId());
            if (auxGrp == null) {
                auxGrp = new HashMap<Integer, AuxDataViewDO>();
                auxGrps.put(aux.getAuxFieldGroupId(), auxGrp);
                grpIds.add(aux.getAuxFieldGroupId());
            }
            auxGrp.put(aux.getAnalyteId(), aux);
            prevGrpId = aux.getAuxFieldGroupId();
        }

        /*
         * fieldAux are consumed by sample, sample domain, project
         */
        auxGrp = auxGrps.get(domainGrpId);
        if (auxGrp != null) {
            copyGeneralFields(sm, auxGrp, e);
            if (Constants.domain().ENVIRONMENTAL.equals(data.getDomain()))
                copyEnvironmentalFields(accession, getSampleEnvironmental(sm), auxGrp, e);
            else if (Constants.domain().PRIVATEWELL.equals(data.getDomain()))
                copyPrivateWellFields(accession, getSamplePrivateWell(sm), auxGrp, e);
            else if (Constants.domain().SDWIS.equals(data.getDomain()))
                copySDWISFields(accession, getSampleSDWIS(sm), auxGrp, e);
            auxGrps.remove(domainGrpId);
            grpIds.remove(domainGrpId);
        }

        /*
         * if there are any other aux groups in the order than add them to the
         * sample
         */
        if (grpIds.size() > 0) {
            if (getAuxiliary(sm) == null)
                setAuxiliary(sm, new ArrayList<AuxDataViewDO>());
            addAuxGroups(getAuxiliary(sm), grpIds, auxGrps, e);
            
            /*
             * set negative ids in the newly added aux data
             */
            for (AuxDataViewDO aux : getAuxiliary(sm)) {
                if (aux.getId() == null)
                    aux.setId(sm.getNextUID());
            }
        }
    }

    /**
     * copy sample data to the aux data of an order, based on a list of aux data
     * external IDs to copy
     */
    public void copyFromSample(SampleManager1 sm, ArrayList<AuxDataViewDO> auxiliary,
                               ArrayList<String> analytes) {
        SampleDO data;
        HashSet<String> anaSet;

        if (analytes == null || auxiliary == null)
            return;

        anaSet = new HashSet<String>(analytes);
        data = getSample(sm);
        if (Constants.domain().ENVIRONMENTAL.equals(data.getDomain()))
            fillEnvironmentalAuxData(sm, auxiliary, anaSet);
        else if (Constants.domain().PRIVATEWELL.equals(data.getDomain()))
            fillPrivateWellAuxData(sm, auxiliary, anaSet);
        else if (Constants.domain().SDWIS.equals(data.getDomain()))
            fillSdwisAuxData(sm, auxiliary, anaSet);
    }

    /**
     * returns the group ids present in the list of ids but not in the list of
     * aux data
     */
    private ArrayList<Integer> getDifference(ArrayList<AuxDataViewDO> auxiliary,
                                             ArrayList<Integer> groupIds) {
        Integer prevId;
        ArrayList<Integer> addIds;

        prevId = null;
        addIds = new ArrayList<Integer>(groupIds);
        for (AuxDataViewDO a : auxiliary) {
            if ( !a.getAuxFieldGroupId().equals(prevId)) {
                if (groupIds.contains(a.getAuxFieldGroupId()))
                    addIds.remove(a.getAuxFieldGroupId());
                prevId = a.getAuxFieldGroupId();
            }
        }
        return addIds;
    }

    /**
     * Sets values of fields independent of domain from the corresponding aux
     * data in the list. Adds warnings or throws exception for invalid data.
     */
    private void copyGeneralFields(SampleManager1 sm, HashMap<Integer, AuxDataViewDO> grp,
                                   ValidationErrorsList e) throws Exception {
        Integer accession;
        String extId;
        SampleDO sample;
        ArrayList<ProjectDO> projects;

        sample = getSample(sm);
        /*
         * for display
         */
        accession = sample.getAccessionNumber();
        if (accession == null)
            accession = 0;
        
        for (AuxDataViewDO data : grp.values()) {
            extId = data.getAnalyteExternalId();
            if (SMPL_COLLECTED_DATE.equals(extId)) {
                sample.setCollectionDate(ReportUtil.getDate(data.getValue()));
            } else if (SMPL_COLLECTED_TIME.equals(extId)) {
                sample.setCollectionTime(ReportUtil.getTime(data.getValue()));
            } else if (SMPL_CLIENT_REF.equals(extId)) {
                sample.setClientReference(data.getValue());
            } else if (PROJECT_NAME.equals(extId) && data.getValue() != null) {
                try {
                    projects = project.fetchActiveByName(data.getValue(), 1);
                    if (projects.size() > 0)
                        sm.project.add(projects.get(0));
                    else
                        e.add(new FormErrorWarning(Messages.get().sample_orderImportException(accession, "project",
                                                                                   data.getValue())));
                } catch (Exception ex) {
                    log.log(Level.SEVERE, "Missing/invalid project '" + data.getValue() + "'", ex);
                    throw ex;
                }
            }
        }
    }

    /**
     * Sets values of environmental fields from the corresponding aux data in
     * the list. Adds warnings or throws exception for invalid data.
     */
    private void copyEnvironmentalFields(Integer accession, SampleEnvironmentalDO env,
                                         HashMap<Integer, AuxDataViewDO> grp, ValidationErrorsList e) throws Exception {
        Integer pr;
        String extId;

        for (AuxDataViewDO data : grp.values()) {
            extId = data.getAnalyteExternalId();
            if (IS_HAZARDOUS.equals(extId)) {
                try {
                    if (data.getValue() != null &&
                        YES.equals(dictionaryCache.getById(new Integer(data.getValue()))
                                                  .getSystemName()))
                        env.setIsHazardous("Y");
                    else
                        env.setIsHazardous("N");
                } catch (NotFoundException ex) {
                    e.add(new FormErrorWarning(Messages.get().sample_orderImportException(accession, "flag hazardous",
                                                                               data.getValue())));
                } catch (Exception ex) {
                    log.log(Level.SEVERE, "Missing/invalid flag hazardous '" + data.getValue() +
                                          "'", ex);
                    throw ex;
                }
            } else if (COLLECTOR.equals(extId)) {
                env.setCollector(data.getValue());
            } else if (LOCATION.equals(extId)) {
                env.setLocation(data.getValue());
            } else if (PRIORITY.equals(extId)) {
                try {
                    pr = null;
                    if (data.getValue() != null)
                        pr = new Integer(data.getValue());
                    env.setPriority(pr);
                } catch (Exception ex) {
                    e.add(new FormErrorWarning(Messages.get().sample_orderImportException(accession, "priority",
                                                                               data.getValue())));
                }
            } else if (COLLECTOR_PHONE.equals(extId)) {
                env.setCollectorPhone(data.getValue());
            } else if (DESCRIPTION.equals(extId)) {
                env.setDescription(data.getValue());
            } else {
                copyAddressFields(accession, data, e, extId, env.getLocationAddress());
            }
        }
    }

    /**
     * Sets values of private well fields from the corresponding aux data in the
     * list. Adds warnings or throws exception for invalid data.
     */
    private void copyPrivateWellFields(Integer accession, SamplePrivateWellViewDO well,
                                       HashMap<Integer, AuxDataViewDO> grp, ValidationErrorsList e) throws Exception {
        Integer w;
        String extId;

        for (AuxDataViewDO data : grp.values()) {
            extId = data.getAnalyteExternalId();
            if (LOCATION.equals(extId)) {
                well.setLocation(data.getValue());
            } else if (OWNER.equals(extId)) {
                well.setOwner(data.getValue());
            } else if (COLLECTOR.equals(extId)) {
                well.setCollector(data.getValue());
            } else if (WELL_NUMBER.equals(extId)) {
                try {
                    w = null;
                    if (data.getValue() != null)
                        w = new Integer(data.getValue());
                    well.setWellNumber(w);
                } catch (Exception ex) {
                    e.add(new FormErrorWarning(Messages.get().sample_orderImportException(accession, "well number",
                                                                               data.getValue())));
                }
            } else {
                copyAddressFields(accession, data, e, extId, well.getLocationAddress());
            }
        }
    }

    /**
     * Sets values of SDWIS fields from the corresponding aux data in the list.
     * Adds warnings or throws exception for invalid data.
     */
    private void copySDWISFields(Integer accession, SampleSDWISViewDO sdwis, HashMap<Integer, AuxDataViewDO> grp,
                                 ValidationErrorsList e) throws Exception {
        Integer dictId, pr;
        String extId;
        PWSDO pwsDO;
        DictionaryDO dict;

        for (AuxDataViewDO data : grp.values()) {
            extId = data.getAnalyteExternalId();
            if (PWS_ID.equals(extId) && data.getValue() != null) {
                try {
                    pwsDO = pws.fetchByNumber0(data.getValue());
                    sdwis.setPwsId(pwsDO.getId());
                    sdwis.setPwsName(pwsDO.getName());
                    sdwis.setPwsNumber0(pwsDO.getNumber0());
                } catch (NotFoundException ex) {
                    e.add(new FormErrorWarning(Messages.get().sample_orderImportException(accession, "pws id",
                                                                               data.getValue())));
                } catch (Exception ex) {
                    log.log(Level.SEVERE, "Missing/invalid pws id '" + data.getValue() + "'", ex);
                    throw ex;
                }
            } else if (STATE_LAB_NUM.equals(extId) && data.getValue() != null) {
                sdwis.setStateLabId(new Integer(data.getValue()));
            } else if (FACILITY_ID.equals(extId)) {
                sdwis.setFacilityId(data.getValue());
            } else if (SAMPLE_TYPE.equals(extId)) {
                if (data.getValue() != null) {
                    try {
                        dictId = new Integer(data.getValue());
                    } catch (Exception ex) {
                        log.log(Level.SEVERE, "Missing/invalid dictionary id '" + data.getValue() +
                                              "'", ex);
                        throw ex;
                    }
                    if ( !isInCategory(SDWIS_SAMPLE_TYPE, dictId)) {
                        e.add(new FormErrorWarning(Messages.get().sample_orderImportException(accession, "sample type",
                                                                                   data.getValue())));
                    } else {
                        dict = dictionaryCache.getById(dictId);
                        /*
                         * set the sample category only if it's active
                         */
                        if ("Y".equals(dict.getIsActive())) {
                            sdwis.setSampleTypeId(dictId);
                        } else {
                            sdwis.setSampleTypeId(null);
                            e.add(new FormErrorWarning(Messages.get()
                                                               .sample_orderImportException(accession,
                                                                                            "sample type",
                                                                                            dict.getEntry())));
                        }
                    }
                }
            } else if (SAMPLE_CAT.equals(extId)) {
                if (data.getValue() != null) {
                    try {
                        dictId = new Integer(data.getValue());
                    } catch (Exception ex) {
                        log.log(Level.SEVERE, "Missing/invalid dictionary id '" + data.getValue() +
                                              "'", ex);
                        throw ex;
                    }
                    if ( !isInCategory(SDWIS_SAMPLE_CATEGORY, dictId)) {
                        e.add(new FormErrorWarning(Messages.get()
                                                           .sample_orderImportException(accession, "sample category",
                                                                             data.getValue())));
                    } else {
                        dict = dictionaryCache.getById(dictId);
                        /*
                         * set the sample category only if it's active
                         */
                        if ("Y".equals(dict.getIsActive())) {
                            sdwis.setSampleCategoryId(dictId);
                        } else {
                            sdwis.setSampleCategoryId(null);
                            e.add(new FormErrorWarning(Messages.get()
                                                               .sample_orderImportException(accession,
                                                                                            "sample category",
                                                                                            dict.getEntry())));
                        }
                    }
                }
            } else if (SAMPLE_PT_ID.equals(extId)) {
                sdwis.setSamplePointId(data.getValue());
            } else if (LOCATION.equals(extId)) {
                sdwis.setLocation(data.getValue());
            } else if (PRIORITY.equals(extId)) {
                try {
                    pr = null;
                    if (data.getValue() != null)
                        pr = new Integer(data.getValue());
                    sdwis.setPriority(pr);
                } catch (Exception ex) {
                    e.add(new FormErrorWarning(Messages.get().sample_orderImportException(accession, "priority",
                                                                               data.getValue())));
                }
            } else if (COLLECTOR.equals(extId)) {
                sdwis.setCollector(data.getValue());
            }
        }
    }

    /**
     * Sets values of address fields from the aux data. Adds warnings for
     * invalid data.
     */
    private void copyAddressFields(Integer accession, AuxDataViewDO data, ValidationErrorsList e, String extId,
                                   AddressDO addr) throws Exception {
        if (LOC_MULT_UNIT.equals(extId)) {
            addr.setMultipleUnit(data.getValue());
        } else if (LOC_STREET_ADDRESS.equals(extId)) {
            addr.setStreetAddress(data.getValue());
        } else if (LOC_CITY.equals(extId)) {
            addr.setCity(data.getValue());
        } else if (LOC_STATE.equals(extId) && data.getValue() != null) {
            if (isInCategory(STATE, data.getValue()))
                addr.setState(data.getValue());
            else
                e.add(new FormErrorWarning(Messages.get().sample_orderImportException(accession, STATE, data.getValue())));
        } else if (LOC_ZIP_CODE.equals(extId)) {
            addr.setZipCode(data.getValue());
        } else if (LOC_COUNTRY.equals(extId) && data.getValue() != null) {
            if (isInCategory(COUNTRY, data.getValue()))
                addr.setCountry(data.getValue());
            else
                e.add(new FormErrorWarning(Messages.get()
                                                   .sample_orderImportException(accession, COUNTRY, data.getValue())));
        }
    }

    /**
     * Returns the id of an aux field group. This id is specific to a group of
     * aux prompts that mimic sample specific fields.
     */
    private Integer getDomainAuxGroupId(SampleDO data) throws Exception {
        String name;
        SystemVariableDO sys;
        AuxFieldGroupDO aux;

        name = null;
        if (Constants.domain().ENVIRONMENTAL.equals(data.getDomain()))
            name = SAMPLE_ENV_AUX_DATA;
        else if (Constants.domain().PRIVATEWELL.equals(data.getDomain()))
            name = SAMPLE_WELL_AUX_DATA;
        else if (Constants.domain().SDWIS.equals(data.getDomain()))
            name = SAMPLE_SDWIS_AUX_DATA;

        /*
         * we don't want to use a hard-coded reference to aux group. So we use a
         * system variable that that points to the aux group.
         */
        try {
            sys = systemVariable.fetchByName(name);
        } catch (NotFoundException ex) {
            return null;
        } catch (Exception ex) {
            log.log(Level.SEVERE, "Missing/invalid system variable '" + name + "'", ex);
            throw ex;
        }

        try {
            aux = auxFieldGroup.fetchActiveByName(sys.getValue());
            return aux.getId();
        } catch (Exception ex) {
            log.log(Level.SEVERE, "Missing/invalid aux field group '" + sys.getValue() + "'", ex);
            throw ex;
        }
    }

    /**
     * Returns true if a dictionary with the specified entry can be found in the
     * category specified by the system name, and false otherwise
     */
    private boolean isInCategory(String systemName, String entry) throws Exception {
        ArrayList<DictionaryDO> entries;

        entries = categoryCache.getBySystemName(systemName).getDictionaryList();
        for (DictionaryDO data : entries) {
            if (DataBaseUtil.isSame(entry, data.getEntry()))
                return true;
        }
        return false;
    }

    /**
     * Returns true if a dictionary with the specified id can be found in the
     * category specified by the system name, and false otherwise
     */
    private boolean isInCategory(String systemName, Integer id) throws Exception {
        ArrayList<DictionaryDO> entries;

        entries = categoryCache.getBySystemName(systemName).getDictionaryList();
        for (DictionaryDO data : entries) {
            if (DataBaseUtil.isSame(id, data.getId()))
                return true;
        }

        return false;
    }

    /**
     * Fill the aux data of an order with the SDWIS data of a sample, based on a
     * list of aux data external IDs to copy. If the aux data is already filled
     * with data, don't fill it with new data.
     */
    private void fillSdwisAuxData(SampleManager1 sm, ArrayList<AuxDataViewDO> auxiliary,
                                  HashSet<String> analytes) {
        String extId;
        SampleSDWISViewDO sdwis;
        SampleDO sample;

        sdwis = getSampleSDWIS(sm);
        sample = getSample(sm);
        for (AuxDataViewDO data : auxiliary) {
            extId = data.getAnalyteExternalId();
            if ( !analytes.contains(extId) || data.getValue() != null)
                continue;
            if (SMPL_COLLECTED_DATE.equals(extId)) {
                data.setTypeId(Constants.dictionary().AUX_DATE);
                data.setValue(DataBaseUtil.toString(sample.getCollectionDate()));
            } else if (SMPL_COLLECTED_TIME.equals(extId)) {
                data.setTypeId(Constants.dictionary().AUX_TIME);
                data.setValue(DataBaseUtil.toString(sample.getCollectionTime()));
            } else if (SMPL_CLIENT_REF.equals(extId)) {
                data.setTypeId(Constants.dictionary().AUX_ALPHA_MIXED);
                data.setValue(sample.getClientReference());
            } else if (PWS_ID.equals(extId)) {
                data.setTypeId(Constants.dictionary().AUX_ALPHA_UPPER);
                data.setValue(sdwis.getPwsNumber0());
            } else if (STATE_LAB_NUM.equals(extId)) {
                data.setTypeId(Constants.dictionary().AUX_NUMERIC);
                data.setValue(DataBaseUtil.toString(sdwis.getStateLabId()));
            } else if (FACILITY_ID.equals(extId)) {
                data.setTypeId(Constants.dictionary().AUX_ALPHA_MIXED);
                data.setValue(sdwis.getFacilityId());
            } else if (SAMPLE_TYPE.equals(extId)) {
                data.setTypeId(Constants.dictionary().AUX_DICTIONARY);
                data.setValue(sdwis.getSampleTypeId().toString());
            } else if (SAMPLE_CAT.equals(extId)) {
                data.setTypeId(Constants.dictionary().AUX_DICTIONARY);
                data.setValue(sdwis.getSampleCategoryId().toString());
            } else if (SAMPLE_PT_ID.equals(extId)) {
                data.setTypeId(Constants.dictionary().AUX_ALPHA_MIXED);
                data.setValue(sdwis.getSamplePointId());
            } else if (LOCATION.equals(extId)) {
                data.setTypeId(Constants.dictionary().AUX_ALPHA_MIXED);
                data.setValue(sdwis.getLocation());
            } else if (PRIORITY.equals(extId)) {
                data.setTypeId(Constants.dictionary().AUX_NUMERIC);
                data.setValue(DataBaseUtil.toString(sdwis.getPriority()));
            } else if (COLLECTOR.equals(extId)) {
                data.setTypeId(Constants.dictionary().AUX_ALPHA_MIXED);
                data.setValue(sdwis.getCollector());
            } else if (LOCATION.equals(extId)) {
                data.setValue(sdwis.getLocation());
            } else if (PROJECT_NAME.equals(extId)) {
                if (getProjects(sm) != null) {
                    for (SampleProjectViewDO p : getProjects(sm)) {
                        if ("Y".equals(p.getIsPermanent())) {
                            data.setTypeId(Constants.dictionary().AUX_ALPHA_LOWER);
                            data.setValue(p.getProjectName());
                            break;
                        }
                    }
                }
            } else if (ORIG_SAMPLE_NUMBER.equals(extId)) {
                data.setTypeId(Constants.dictionary().AUX_NUMERIC);
                data.setValue(sample.getAccessionNumber().toString());
            }
        }
    }

    /**
     * Fill the aux data of an order with the environmental data of a sample,
     * based on a list of aux data external IDs to copy. If the aux data is
     * already filled with data, don't fill it with new data.
     */
    private void fillEnvironmentalAuxData(SampleManager1 sm, ArrayList<AuxDataViewDO> auxiliary,
                                          HashSet<String> analytes) {
        String extId;
        SampleEnvironmentalDO env;
        SampleDO sample;
        AddressDO address;

        env = getSampleEnvironmental(sm);
        address = env.getLocationAddress();
        sample = getSample(sm);
        for (AuxDataViewDO data : auxiliary) {
            extId = data.getAnalyteExternalId();
            if ( !analytes.contains(extId) || data.getValue() != null)
                continue;
            if (SMPL_COLLECTED_DATE.equals(extId)) {
                data.setTypeId(Constants.dictionary().AUX_DATE);
                data.setValue(DataBaseUtil.toString(sample.getCollectionDate()));
            } else if (SMPL_COLLECTED_TIME.equals(extId)) {
                data.setTypeId(Constants.dictionary().AUX_TIME);
                data.setValue(DataBaseUtil.toString(sample.getCollectionTime()));
            } else if (SMPL_CLIENT_REF.equals(extId)) {
                data.setTypeId(Constants.dictionary().AUX_ALPHA_MIXED);
                data.setValue(sample.getClientReference());
            } else if (IS_HAZARDOUS.equals(extId)) {
                data.setTypeId(Constants.dictionary().AUX_DICTIONARY);
                data.setValue(env.getIsHazardous());
            } else if (COLLECTOR.equals(extId)) {
                data.setTypeId(Constants.dictionary().AUX_ALPHA_MIXED);
                data.setValue(env.getCollector());
            } else if (LOCATION.equals(extId)) {
                data.setTypeId(Constants.dictionary().AUX_ALPHA_MIXED);
                data.setValue(env.getLocation());
            } else if (LOC_MULT_UNIT.equals(extId) && address != null) {
                data.setTypeId(Constants.dictionary().AUX_ALPHA_UPPER);
                data.setValue(address.getMultipleUnit());
            } else if (LOC_STREET_ADDRESS.equals(extId) && address != null) {
                data.setTypeId(Constants.dictionary().AUX_ALPHA_UPPER);
                data.setValue(address.getStreetAddress());
            } else if (LOC_CITY.equals(extId) && address != null) {
                data.setTypeId(Constants.dictionary().AUX_ALPHA_UPPER);
                data.setValue(address.getCity());
            } else if (LOC_STATE.equals(extId) && address != null) {
                data.setTypeId(Constants.dictionary().AUX_ALPHA_UPPER);
                data.setValue(address.getState());
            } else if (LOC_ZIP_CODE.equals(extId) && address != null) {
                data.setTypeId(Constants.dictionary().AUX_ALPHA_MIXED);
                data.setValue(address.getZipCode());
            } else if (LOC_COUNTRY.equals(extId) && address != null) {
                data.setTypeId(Constants.dictionary().AUX_ALPHA_MIXED);
                data.setValue(address.getCountry());
            } else if (PRIORITY.equals(extId)) {
                data.setTypeId(Constants.dictionary().AUX_NUMERIC);
                data.setValue(DataBaseUtil.toString(env.getPriority()));
            } else if (COLLECTOR_PHONE.equals(extId)) {
                data.setTypeId(Constants.dictionary().AUX_ALPHA_MIXED);
                data.setValue(env.getCollectorPhone());
            } else if (DESCRIPTION.equals(extId)) {
                data.setTypeId(Constants.dictionary().AUX_ALPHA_MIXED);
                data.setValue(env.getDescription());
            } else if (PROJECT_NAME.equals(extId)) {
                if (getProjects(sm) != null) {
                    for (SampleProjectViewDO p : getProjects(sm)) {
                        if ("Y".equals(p.getIsPermanent())) {
                            data.setTypeId(Constants.dictionary().AUX_ALPHA_MIXED);
                            data.setValue(p.getProjectName());
                            break;
                        }
                    }
                }
            } else if (ORIG_SAMPLE_NUMBER.equals(extId)) {
                data.setTypeId(Constants.dictionary().AUX_NUMERIC);
                data.setValue(sample.getAccessionNumber().toString());
            }
        }
    }

    /**
     * Fill the aux data of an order with the private well data of a sample,
     * based on a list of aux data external IDs to copy. If the aux data is
     * already filled with data, don't fill it with new data.
     */
    private void fillPrivateWellAuxData(SampleManager1 sm, ArrayList<AuxDataViewDO> auxiliary,
                                        HashSet<String> analytes) {
        String extId;
        SamplePrivateWellViewDO well;
        SampleDO sample;
        AddressDO address;

        well = getSamplePrivateWell(sm);
        address = well.getLocationAddress();
        sample = getSample(sm);
        for (AuxDataViewDO data : auxiliary) {
            extId = data.getAnalyteExternalId();
            if ( !analytes.contains(extId) || data.getValue() != null)
                continue;
            if (SMPL_COLLECTED_DATE.equals(extId)) {
                data.setTypeId(Constants.dictionary().AUX_DATE);
                data.setValue(DataBaseUtil.toString(sample.getCollectionDate()));
            } else if (SMPL_COLLECTED_TIME.equals(extId)) {
                data.setTypeId(Constants.dictionary().AUX_TIME);
                data.setValue(DataBaseUtil.toString(sample.getCollectionTime()));
            } else if (SMPL_CLIENT_REF.equals(extId)) {
                data.setTypeId(Constants.dictionary().AUX_ALPHA_MIXED);
                data.setValue(sample.getClientReference());
            } else if (LOCATION.equals(extId)) {
                data.setTypeId(Constants.dictionary().AUX_ALPHA_MIXED);
                data.setValue(well.getLocation());
            } else if (LOC_MULT_UNIT.equals(extId) && address != null) {
                data.setTypeId(Constants.dictionary().AUX_ALPHA_UPPER);
                data.setValue(address.getMultipleUnit());
            } else if (LOC_STREET_ADDRESS.equals(extId) && address != null) {
                data.setTypeId(Constants.dictionary().AUX_ALPHA_UPPER);
                data.setValue(address.getStreetAddress());
            } else if (LOC_CITY.equals(extId) && address != null) {
                data.setTypeId(Constants.dictionary().AUX_ALPHA_UPPER);
                data.setValue(address.getCity());
            } else if (LOC_STATE.equals(extId) && address != null) {
                data.setTypeId(Constants.dictionary().AUX_ALPHA_UPPER);
                data.setValue(address.getState());
            } else if (LOC_ZIP_CODE.equals(extId) && address != null) {
                data.setTypeId(Constants.dictionary().AUX_ALPHA_MIXED);
                data.setValue(address.getZipCode());
            } else if (OWNER.equals(extId)) {
                data.setTypeId(Constants.dictionary().AUX_ALPHA_MIXED);
                data.setValue(well.getOwner());
            } else if (COLLECTOR.equals(extId)) {
                data.setTypeId(Constants.dictionary().AUX_ALPHA_MIXED);
                data.setValue(well.getCollector());
            } else if (WELL_NUMBER.equals(extId)) {
                data.setTypeId(Constants.dictionary().AUX_NUMERIC);
                data.setValue(DataBaseUtil.toString(well.getWellNumber()));
            } else if (PROJECT_NAME.equals(extId)) {
                if (getProjects(sm) != null) {
                    for (SampleProjectViewDO p : getProjects(sm)) {
                        if ("Y".equals(p.getIsPermanent())) {
                            data.setTypeId(Constants.dictionary().AUX_ALPHA_MIXED);
                            data.setValue(p.getProjectName());
                            break;
                        }
                    }
                }
            } else if (ORIG_SAMPLE_NUMBER.equals(extId)) {
                data.setTypeId(Constants.dictionary().AUX_NUMERIC);
                data.setValue(sample.getAccessionNumber().toString());
            }
        }
    }

    public HashMap<String, String> fillAirQualityAuxData(SampleManager1 sm) throws Exception {
        AuxDataViewDO data;
        HashMap<String, String> auxDataValues;
        ArrayList<AuxDataViewDO> auxiliary;

        auxiliary = getAuxiliary(sm);
        auxDataValues = new HashMap<String, String>();
        for (int i = 0; i < auxiliary.size(); i++ ) {
            data = auxiliary.get(i);
            if (data.getDictionary() != null) {
                if ( !NULL_DATA_CODE.equals(data.getAnalyteExternalId())) {
                    auxDataValues.put(data.getAnalyteExternalId(), data.getDictionary());
                } else {
                    auxDataValues.put(data.getAnalyteExternalId(),
                                      dictionaryCache.getById(Integer.parseInt(data.getValue()))
                                                     .getCode());
                }
            } else {
                auxDataValues.put(data.getAnalyteExternalId(), data.getValue());
            }
        }
        return auxDataValues;
    }
}