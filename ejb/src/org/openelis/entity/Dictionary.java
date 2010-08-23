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
package org.openelis.entity;

/**
 * Dictionary Entity POJO for database
 */

import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.ReferenceTable;
import org.openelis.utilcommon.DataBaseUtil;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries( {
    @NamedQuery( name = "Dictionary.FetchByCategoryId",
                query = "select distinct new org.openelis.domain.DictionaryViewDO(d.id,d.sortOrder," +
                   		"d.categoryId, d.relatedEntryId, d.systemName,d.isActive,  d.localAbbrev, d.entry, dre.entry)"
                      + " from  Dictionary d left join d.relatedEntry dre  where d.categoryId = :id order by d.sortOrder "),
    @NamedQuery( name = "Dictionary.FetchBySystemName",
                query = "select distinct new org.openelis.domain.DictionaryDO(d.id,d.sortOrder, d.categoryId, d.relatedEntryId," +
                        "d.systemName,d.isActive,  d.localAbbrev, d.entry)"
                      + " from  Dictionary d where d.systemName = :name "),
    @NamedQuery( name = "Dictionary.FetchById",
                query = "select distinct new org.openelis.domain.DictionaryViewDO(d.id,d.sortOrder, d.categoryId, d.relatedEntryId," +
                        "d.systemName,d.isActive,  d.localAbbrev, d.entry, dre.entry)"
                      + " from  Dictionary d left join d.relatedEntry dre where d.id = :id"),
    @NamedQuery( name = "Dictionary.FetchByCategorySystemName",
                query = "select distinct new org.openelis.domain.DictionaryDO(d.id,d.sortOrder, d.categoryId, d.relatedEntryId," +
                        "d.systemName,d.isActive,  d.localAbbrev, d.entry)"
                      + " from  Dictionary d left join d.category c where c.systemName = :name order by d.sortOrder "),                                        
    @NamedQuery( name = "Dictionary.FetchByEntry",
                query = "select distinct new org.openelis.domain.DictionaryDO(d.id,d.sortOrder, d.categoryId, d.relatedEntryId," +
                        "d.systemName,d.isActive,  d.localAbbrev, d.entry)"
                      + " from  Dictionary d left join d.category c where c.isSystem = 'N' and d.entry like :entry")})
                                  
 @NamedNativeQueries({@NamedNativeQuery(name = "Dictionary.ReferenceCheckForId",     
                  query = "select operation_id as DICTIONARY_ID from project_parameter where operation_id = :id " +
                          "UNION " +
                          "select type_id as DICTIONARY_ID from provider where type_id = :id " +
                          "UNION " +
                          "select type_id as DICTIONARY_ID from qaevent where type_id = :id " +
                          "UNION " +
                          "select type_id as DICTIONARY_ID from qc where type_id = :id " +
                          "UNION " +
                          "select prepared_unit_id as DICTIONARY_ID from qc where prepared_unit_id = :id " +
                          "UNION " +
                          "select type_id as DICTIONARY_ID from qc_analyte where type_id = :id " +
                          "UNION " +
                          "select type_id as DICTIONARY_ID from test_analyte where type_id = :id " +
                          "UNION " +
                          "select flags_id as DICTIONARY_ID from test_reflex where flags_id = :id "+
                          "UNION " +
                          "select unit_of_measure_id as DICTIONARY_ID from test_result where unit_of_measure_id = :id "+
                          "UNION " +
                          "select type_id as DICTIONARY_ID from test_result where type_id = :id "+
                          "UNION " +
                          "select rounding_method_id as DICTIONARY_ID from test_result where rounding_method_id = :id "+
                          "UNION " +
                          "select flags_id as DICTIONARY_ID from test_result where flags_id = :id "+
                          "UNION " +
                          "select flag_id as DICTIONARY_ID from test_section where flag_id = :id "+
                          "UNION " +
                          "select type_of_sample_id as DICTIONARY_ID from test_type_of_sample where type_of_sample_id = :id "+
                          "UNION " +
                          "select unit_of_measure_id as DICTIONARY_ID from test_type_of_sample where unit_of_measure_id = :id "+
                          "UNION " +
                          "select format_id as DICTIONARY_ID from test_worksheet where format_id = :id "+
                          "UNION " +
                          "select flag_id as DICTIONARY_ID from test_worksheet_analyte where flag_id = :id "+
                          "UNION " +
                          "select type_id as DICTIONARY_ID from test_worksheet_item where type_id = :id "+
                          "UNION " +
                          "select status_id as DICTIONARY_ID from worksheet where status_id = :id "+
                          "UNION " +
                          "select format_id as DICTIONARY_ID from worksheet where format_id = :id "+
                          "UNION " +
                          "select contact_type_id as DICTIONARY_ID from organization_contact where contact_type_id = :id "+
                          "UNION " +
                          "select type_id as DICTIONARY_ID from organization_parameter where type_id = :id "+
                          "UNION " +
                          "select status_id as DICTIONARY_ID from analysis where status_id = :id "+
                          "UNION " +
                          "select unit_of_measure_id as DICTIONARY_ID from analysis where unit_of_measure_id = :id "+
                          "UNION " +
                          "select type_id as DICTIONARY_ID from analysis_qaevent where type_id = :id "+
                          "UNION " +
                          "select status_id as DICTIONARY_ID from sample where status_id = :id "+
                          "UNION " +
                          "select type_of_sample_id as DICTIONARY_ID from sample_item where type_of_sample_id = :id "+
                          "UNION " +
                          "select source_of_sample_id as DICTIONARY_ID from sample_item where source_of_sample_id = :id "+
                          "UNION " +
                          "select unit_of_measure_id as DICTIONARY_ID from sample_item where unit_of_measure_id = :id "+
                          "UNION " +
                          "select printer_type_id as DICTIONARY_ID from label where printer_type_id = :id "+
                          "UNION " +
                          "select type_id as DICTIONARY_ID from aux_data where type_id = :id "+
                          "UNION " +
                          "select unit_of_measure_id as DICTIONARY_ID from aux_field where unit_of_measure_id = :id "+
                          "UNION " +
                          "select type_id as DICTIONARY_ID from aux_field_value where type_id = :id "+
                          "UNION " +
                          "select category_id as DICTIONARY_ID from storage_unit where category_id = :id "+
                          "UNION " +
                          "select type_id as DICTIONARY_ID from instrument where type_id = :id "+
                          "UNION " +
                          "select type_id as DICTIONARY_ID from instrument_log where type_id = :id "+
                          "UNION " +
                          "select category_id as DICTIONARY_ID from inventory_item where category_id = :id "+
                          "UNION " +
                          "select store_id as DICTIONARY_ID from inventory_item where store_id = :id "+
                          "UNION " +
                          "select dispensed_units_id as DICTIONARY_ID from inventory_item where dispensed_units_id = :id "+
                          "UNION " +
                          "select action_id as DICTIONARY_ID from analysis_user where action_id = :id "+
                          "UNION " +
                          "select type_id as DICTIONARY_ID from attachment where type_id = :id "+
                          "UNION " +
                          "select type_id as DICTIONARY_ID from result where type_id = :id "+
                          "UNION " +
                          "select animal_common_name_id as DICTIONARY_ID from sample_animal where animal_common_name_id = :id "+
                          "UNION " +
                          "select animal_scientific_name_id as DICTIONARY_ID from sample_animal where animal_scientific_name_id = :id "+
                          "UNION " +
                          "select container_id as DICTIONARY_ID from sample_item where container_id = :id "+
                          "UNION " +
                          "select type_id as DICTIONARY_ID from sample_organization where type_id = :id "+
                          "UNION " +
                          "select type_id as DICTIONARY_ID from sample_qaevent where type_id = :id "+
                          "UNION " +
                          "select sample_type_id as DICTIONARY_ID from sample_sdwis where sample_type_id = :id "+
                          "UNION " +
                          "select sample_category_id as DICTIONARY_ID from sample_sdwis where sample_category_id = :id "+
                          "UNION " +
                          "select status_id as DICTIONARY_ID from shipping where status_id = :id "+
                          "UNION " +
                          "select test_format_id as DICTIONARY_ID from test where test_format_id = :id "+
                          "UNION " +
                          "select revision_method_id as DICTIONARY_ID from test where revision_method_id = :id "+
                          "UNION " +
                          "select reporting_method_id as DICTIONARY_ID from test where reporting_method_id = :id "+
                          "UNION " +
                          "select sorting_method_id as DICTIONARY_ID from test where sorting_method_id = :id "+
                          "UNION " +
                          "select type_id as DICTIONARY_ID from worksheet_qc_result where type_id = :id "+
                          "UNION " +
                          "select type_id as DICTIONARY_ID from worksheet_result where type_id = :id "+
                          "UNION " +                          
                          "select status_id as DICTIONARY_ID from order where status_id = :id "+
                          "UNION " +
                          "select cost_center_id as DICTIONARY_ID from order where cost_center_id = :id "+
                          "UNION " +
                          "select container_id as DICTIONARY_ID from order_container where container_id = :id "+
                          "UNION " +
                          "select type_of_sample_id as DICTIONARY_ID from order_container where type_of_sample_id = :id "+
                          "UNION " +
                          "select gender_id as DICTIONARY_ID from patient where gender_id = :id "+
                          "UNION " +
                          "select race_id as DICTIONARY_ID from patient where race_id = :id "+
                          "UNION " +
                          "select ethnicity_id as DICTIONARY_ID from patient where ethnicity_id = :id "+
                          "UNION " +
                          "select relation_id as DICTIONARY_ID from patient_relation where relation_id = :id "+
                          "UNION " +
                          "select type_id as DICTIONARY_ID from standard_note where type_id = :id ",                          
                  resultSetMapping="Dictionary.ReferenceCheckForIdMapping"),
                  @NamedNativeQuery(name = "Dictionary.ReferenceCheckForValue",     
                              query = "select value as VALUE from test_result tr,dictionary d where value = :value and " +
                              		  " tr.type_id = d.id and d.system_name = 'test_res_type_dictionary' " +
                                      " UNION " +
                                      "select value as VALUE from qc_analyte qca,dictionary d where value = :value and" +
                                      " qca.type_id = d.id and d.system_name = 'qc_analyte_dictionary' " +
                                      " UNION " +
                                      "select value as VALUE from aux_field_value afv,dictionary d where value = :value and" +
                                      " afv.type_id = d.id and d.system_name = 'aux_dictionary' ",                          
                  resultSetMapping="Dictionary.ReferenceCheckForValueMapping"),
                  @NamedNativeQuery(name = "Dictionary.ReferenceCheckForEntry",     
                                    query = "select state as ENTRY from address where state = :entry " +
                                            "UNION " +
                                            "select country as ENTRY from address where country = :entry ",                       
                                    resultSetMapping="Dictionary.ReferenceCheckForEntryMapping")})
                                    
@SqlResultSetMappings({@SqlResultSetMapping(name="Dictionary.ReferenceCheckForIdMapping",
                     columns={@ColumnResult(name="DICTIONARY_ID")}),
@SqlResultSetMapping(name="Dictionary.ReferenceCheckForValueMapping",
                     columns={@ColumnResult(name="VALUE")}),
@SqlResultSetMapping(name="Dictionary.ReferenceCheckForEntryMapping",
                     columns={@ColumnResult(name="ENTRY")})})                     
                     
                     
                     
    
@Entity
@Table(name = "dictionary")
@EntityListeners( {AuditUtil.class})
public class Dictionary implements Auditable, Cloneable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer    id;

    @Column(name = "category_id")
    private Integer    categoryId;

    @Column(name = "sort_order")
    private Integer    sortOrder;

    @Column(name = "related_entry_id")
    private Integer    relatedEntryId;

    @Column(name = "system_name")
    private String     systemName;

    @Column(name = "is_active")
    private String     isActive;

    @Column(name = "local_abbrev")
    private String     localAbbrev;

    @Column(name = "entry")
    private String     entry;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_entry_id", insertable = false, updatable = false)
    private Dictionary relatedEntry;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    private Category   category;

    @Transient
    private Dictionary original;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        if (DataBaseUtil.isDifferent(categoryId, this.categoryId))
            this.categoryId = categoryId;
    }

    public void setSortOrder(Integer sortOrder) {
        if (DataBaseUtil.isDifferent(sortOrder, this.sortOrder))
            this.sortOrder = sortOrder;
    }

    public Integer getRelatedEntryId() {
        return relatedEntryId;
    }

    public void setRelatedEntryId(Integer relatedEntryId) {
        if (DataBaseUtil.isDifferent(relatedEntryId, this.relatedEntryId))
            this.relatedEntryId = relatedEntryId;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        if (DataBaseUtil.isDifferent(systemName, this.systemName))
            this.systemName = systemName;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        if (DataBaseUtil.isDifferent(isActive, this.isActive))
            this.isActive = isActive;
    }

    public String getLocalAbbrev() {
        return localAbbrev;
    }

    public void setLocalAbbrev(String localAbbrev) {
        if (DataBaseUtil.isDifferent(localAbbrev, this.localAbbrev))
            this.localAbbrev = localAbbrev;
    }

    public String getEntry() {
        return entry;
    }

    public void setEntry(String entry) {
        if (DataBaseUtil.isDifferent(entry, this.entry))
            this.entry = entry;
    }
    
    public Dictionary getRelatedEntry() {
        return relatedEntry;
    }

    public void setRelatedEntry(Dictionary relatedEntryRow) {
        this.relatedEntry = relatedEntryRow;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setClone() {
        try {
            original = (Dictionary)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public Audit getAudit() {
        Audit audit;

        audit = new Audit();
        audit.setReferenceTableId(ReferenceTable.DICTIONARY);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("category_id", categoryId, original.categoryId)
                 .setField("sort_order", sortOrder, original.sortOrder)
                 .setField("related_entry_id", relatedEntryId, original.relatedEntryId, ReferenceTable.DICTIONARY)
                 .setField("system_name", systemName, original.systemName)
                 .setField("is_active", isActive, original.isActive)
                 .setField("local_abbrev", localAbbrev, original.localAbbrev)
                 .setField("entry", entry, original.entry);

        return audit;
    }

}
