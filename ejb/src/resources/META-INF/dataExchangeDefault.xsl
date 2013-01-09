<!--
Exhibit A - UIRF Open-source Based Public Software License.
  
The contents of this file are subject to the UIRF Open-source Based
Public Software License(the "License"); you may not use this file except
in compliance with the License. You may obtain a copy of the License at
openelis.uhl.uiowa.edu
  
Software distributed under the License is distributed on an "AS IS"
basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
License for the specific language governing rights and limitations
under the License.
  
The Original Code is OpenELIS code.
  
The Initial Developer of the Original Code is The University of Iowa.
Portions created by The University of Iowa are Copyright 2006-2008. All
Rights Reserved.
  
Contributor(s): ______________________________________.
  
Alternatively, the contents of this file marked
"Separately-Licensed" may be used under the terms of a UIRF Software
license ("UIRF Software License"), in which case the provisions of a
UIRF Software License are applicable instead of those above. 
  -->
<xsl:stylesheet version="1.0" 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  
  <xsl:template match="message">
    <!--
        The following attribute is called "Type" and not "type" because "type"
        is treated as a keyword by the module used for data exchange and is 
        not allowed to be used as the name of an attribute or element   
     --> 
    <message Type = "{@type}">
      <xsl:apply-templates select="//header"/>
      <xsl:apply-templates select="//sample" />
    </message>
  </xsl:template>
  <xsl:template match="header">
   <!--
     The information about the exchange criteria if one was used to genarate the
     data for this document  
    -->
    <header>
      <xsl:attribute name="include_all_analyses">
        <xsl:value-of select="@include_all_analyses" />
      </xsl:attribute>
      <xsl:if test="name[. != '']">
        <name>
          <xsl:value-of select="name" />
        </name>
      </xsl:if>
      <xsl:if test="environment[. != '']">
        <environment>
          <xsl:value-of select="environment" />
        </environment>
      </xsl:if>
      <xsl:if test="profiles[. != '']">
        <profiles>
          <xsl:for-each select="profiles">            
             <xsl:apply-templates select="profile" />
          </xsl:for-each>
        </profiles>
      </xsl:if>
    </header>
  </xsl:template>  
  
  <xsl:template match="sample">
    <sample accession_number="{@accession_number}" collection_date="{@collection_date}" domain="{@domain}" entered_date="{@entered_date}" id="{@id}" received_date="{@received_date}" revision="{@revision}">
      <xsl:if test="@order_id[. != '']">
        <xsl:attribute name="order_id">
          <xsl:value-of select="@order_id" />
        </xsl:attribute>
      </xsl:if>
      <xsl:if test="@collection_time[. != '']">
        <xsl:attribute name="collection_time">
          <xsl:value-of select="@collection_time" />
        </xsl:attribute>
      </xsl:if>
      <xsl:if test="@package_id[. != '']">
        <xsl:attribute name="package_id">
          <xsl:value-of select="@package_id" />
        </xsl:attribute>
      </xsl:if>
      <xsl:if test="@released_date[. != '']">
        <xsl:attribute name="released_date">
          <xsl:value-of select="@released_date" />
        </xsl:attribute>
      </xsl:if>
      <xsl:variable name="received_by_id" select="@received_by_id" />
      <received_by>
        <xsl:apply-templates select="//system_user[@id = $received_by_id]" />
      </received_by>
      <xsl:variable name="status_id" select="@status_id" />
      <status>
        <xsl:apply-templates select="//dictionary[@id = $status_id]" />
      </status>
      <xsl:if test="client_reference[. != '']">
        <client_reference>
          <xsl:value-of select="client_reference" />
        </client_reference>
      </xsl:if>
      <xsl:variable name="sample_id" select="@id" />
      <!--
        The information specific to each domain        
      -->
      <sample_domain>    
        <xsl:choose>
          <xsl:when test="@domain = 'E'">
            <xsl:apply-templates select="//sample_environmental[@sample_id = $sample_id]" />
          </xsl:when>
          <xsl:when test="@domain = 'W'">
            <xsl:apply-templates select="//sample_private_well[@sample_id = $sample_id]" />
          </xsl:when>
          <xsl:when test="@domain = 'S'">
            <xsl:apply-templates select="//sample_sdwis[@sample_id = $sample_id]" />
          </xsl:when>
        </xsl:choose>
      </sample_domain>
      <!--
        The items for this sample
      -->
      <xsl:apply-templates select="//sample_item[@sample_id = $sample_id]" />
      <!--
        The projects for this sample
      -->
      <xsl:apply-templates select="//sample_project[@sample_id = $sample_id]" />
      <!-- 
        The organizations for this sample
      -->
      <xsl:apply-templates select="//sample_organization[@sample_id = $sample_id]" />
      <!-- 
        The notes for this sample
      -->
      <xsl:apply-templates select="//sample_external_notes/note[@reference_id = $sample_id]" />
      <!-- 
        The qa events for this sample
      -->
      <xsl:apply-templates select="//sample_qaevent[@sample_id = $sample_id]" />
      <!-- 
        The aux data for this sample
      -->
      <xsl:apply-templates select="//aux_data[@reference_id = $sample_id]" />
    </sample>
  </xsl:template>
  
  <xsl:template match="profile">
    <!-- 
       One of the profiles used for the translations in this document  
       This element is copied from the original xml as is
    -->
    <profile>    
      <xsl:value-of select="." />
    </profile>
  </xsl:template>
  
  <xsl:template match="sample_environmental">
   <!--
        This following attribute allows the element "sample_domain" to have fields 
        belonging to different elements at different times and enable the xml 
        document to stay valid w.r.t the schema. This attribute in the xml document
        indicates the specific type to use for validation for the fields specific 
        to a given domain, and the common fields are validated by the type "sampleDomain".
        See the schema for more details.         
    -->     
    <xsl:attribute name="xsi:type">
      <xsl:value-of select="'sampleEnvironmental'" />
    </xsl:attribute>
    <xsl:attribute name="id">
      <xsl:value-of select="@id" />
    </xsl:attribute>
    <xsl:attribute name="sample_id">
      <xsl:value-of select="@sample_id" />
    </xsl:attribute>
    <xsl:if test="@is_hazardous[. != '']">
      <xsl:attribute name="is_hazardous">
        <xsl:value-of select="@is_hazardous" />
      </xsl:attribute>
    </xsl:if>
    <xsl:if test="@priority[. != '']">
      <xsl:attribute name="priority">
        <xsl:value-of select="@priority" />
      </xsl:attribute>
    </xsl:if>
    <xsl:if test="description[. != '']">
      <description>
        <xsl:value-of select="description" />
      </description>
    </xsl:if>
    <xsl:if test="collector[. != '']">
      <collector>
        <xsl:value-of select="collector" />
      </collector>
    </xsl:if>
    <xsl:if test="collector_phone[. != '']">
      <collector_phone>
        <xsl:value-of select="collector_phone" />
      </collector_phone>
    </xsl:if>
    <xsl:if test="location[. != '']">
      <location>
        <xsl:value-of select="location" />
      </location>
    </xsl:if>
    <xsl:if test="@location_address_id[. != '']">
      <xsl:variable name="location_address_id" select="@location_address_id" />
      <location_address>
        <xsl:apply-templates select="//address[@id = $location_address_id]" />
      </location_address>
    </xsl:if>
  </xsl:template>
  
  <xsl:template match="sample_private_well">
     <!--
        This following attribute allows the element "sample_domain" to have fields 
        belonging to different elements at different times and enable the xml 
        document to stay valid w.r.t the schema. This attribute in the xml document
        indicates the specific type to use for validation for the fields specific 
        to a given domain, and the common fields are validated by the type "sampleDomain".
        See the schema for more details.         
    -->
    <xsl:attribute name="xsi:type">
      <xsl:value-of select="'samplePrivateWell'" />
    </xsl:attribute>
    <xsl:attribute name="id">
      <xsl:value-of select="@id" />
    </xsl:attribute>
    <xsl:attribute name="sample_id">
      <xsl:value-of select="@sample_id" />
    </xsl:attribute>
    <xsl:if test="@organization_id[. != '']">
      <xsl:variable name="organization_id" select="@organization_id" />
      <organization>
        <xsl:apply-templates select="//organization[@id = $organization_id]">
          <xsl:with-param name="attention">
            <xsl:value-of select="report_to_attention" />
          </xsl:with-param>
        </xsl:apply-templates>
      </organization>
    </xsl:if>
    <xsl:if test="@report_to_address_id[. != '']">
      <report_to>
        <xsl:if test="report_to_name[. != '']">
          <name>
            <xsl:value-of select="report_to_name" />
          </name>
        </xsl:if>
        <xsl:if test="report_to_attention[. != '']">
          <attention>
            <xsl:value-of select="report_to_attention" />
          </attention>
        </xsl:if>
        <xsl:variable name="report_to_address_id" select="@report_to_address_id" />
        <address>
          <xsl:apply-templates select="//address[@id = $report_to_address_id]" />
        </address>
      </report_to>
    </xsl:if>
    <xsl:if test="owner[. != '']">
      <owner>
        <xsl:value-of select="owner" />
      </owner>
    </xsl:if>
    <xsl:if test="collector[. != '']">
      <collector>
        <xsl:value-of select="collector" />
      </collector>
    </xsl:if>
    <xsl:if test="well_number[. != '']">
      <well_number>
        <xsl:value-of select="well_number" />
      </well_number>
    </xsl:if>
    <xsl:if test="location[. != '']">
      <location>
        <xsl:value-of select="location" />
      </location>
    </xsl:if>
    <xsl:if test="@location_address_id[. != '']">
      <xsl:variable name="location_address_id" select="@location_address_id" />
      <location_address>
        <xsl:apply-templates select="//address[@id = $location_address_id]" />
      </location_address>
    </xsl:if>
  </xsl:template>
  
  <xsl:template match="sample_sdwis">
    <!--
      This following attribute allows the element "sample_domain" to have fields 
      belonging to different elements at different times and enable the xml 
      document to stay valid w.r.t the schema. This attribute in the xml document
      indicates the specific type to use for validation for the fields specific 
      to a given domain, and the common fields are validated by the type "sampleDomain".
      See the schema for more details.         
    -->
    <xsl:attribute name="xsi:type">
      <xsl:value-of select="'sampleSDWIS'" />
    </xsl:attribute>
    <xsl:attribute name="id">
      <xsl:value-of select="@id" />
    </xsl:attribute>
    <xsl:attribute name="sample_id">
      <xsl:value-of select="@sample_id" />
    </xsl:attribute>
    <xsl:if test="@state_lab_id[. != '']">
      <xsl:attribute name="state_lab_id">
        <xsl:value-of select="@state_lab_id" />
      </xsl:attribute>
    </xsl:if>
    <xsl:variable name="pws_id" select="@pws_id" />
    <pws>
      <xsl:apply-templates select="//pws[@id = $pws_id]" />
    </pws>
    <xsl:if test="facility_id[. != '']">
      <facility_id>
        <xsl:value-of select="facility_id" />
      </facility_id>
    </xsl:if>
    <xsl:variable name="sample_type_id" select="@sample_type_id" />
    <sample_type>
      <xsl:apply-templates select="//dictionary[@id = $sample_type_id]" />
    </sample_type>
    <xsl:variable name="sample_category_id" select="@sample_category_id" />
    <sample_category>
      <xsl:apply-templates select="//dictionary[@id = $sample_category_id]" />
    </sample_category>
    <sample_point_id>
      <xsl:value-of select="sample_point_id" />
    </sample_point_id>
    <xsl:if test="location[. != '']">
      <location>
        <xsl:value-of select="location" />
      </location>
    </xsl:if>
    <xsl:if test="collector[. != '']">
      <collector>
        <xsl:value-of select="collector" />
      </collector>
    </xsl:if>
  </xsl:template>
  
  <xsl:template match="pws">
    <xsl:attribute name="id">
      <xsl:value-of select="@id" />
    </xsl:attribute>
    <xsl:attribute name="tinwsys_is_number">
      <xsl:value-of select="@tinwsys_is_number" />
    </xsl:attribute>
    <number0>
      <xsl:value-of select="number0" />
    </number0>
    <xsl:if test="alternate_st_num[. != '']">
      <alternate_st_num>
        <xsl:value-of select="alternate_st_num" />
      </alternate_st_num>
    </xsl:if>
    <xsl:if test="name[. != '']">
      <name>
        <xsl:value-of select="name" />
      </name>
    </xsl:if>
    <xsl:if test="activity_status_cd[. != '']">
      <activity_status_cd>
        <xsl:value-of select="activity_status_cd" />
      </activity_status_cd>
    </xsl:if>
    <xsl:if test="d_prin_city_svd_nm[. != '']">
      <d_prin_city_svd_nm>
        <xsl:value-of select="d_prin_city_svd_nm" />
      </d_prin_city_svd_nm>
    </xsl:if>
    <xsl:if test="d_prin_cnty_svd_nm[. != '']">
      <d_prin_cnty_svd_nm>
        <xsl:value-of select="d_prin_cnty_svd_nm" />
      </d_prin_cnty_svd_nm>
    </xsl:if>
    <xsl:if test="d_population_count[. != '']">
      <d_population_count>
        <xsl:value-of select="d_population_count" />
      </d_population_count>
    </xsl:if>
    <xsl:if test="d_pws_st_type_cd[. != '']">
      <d_pws_st_type_cd>
        <xsl:value-of select="d_pws_st_type_cd" />
      </d_pws_st_type_cd>
    </xsl:if>
    <xsl:if test="activity_rsn_txt[. != '']">
      <activity_rsn_txt>
        <xsl:value-of select="activity_rsn_txt" />
      </activity_rsn_txt>
    </xsl:if>   
  </xsl:template>
  
  <xsl:template match="sample_item">
    <sample_item>
      <xsl:attribute name="id">
        <xsl:value-of select="@id" />
      </xsl:attribute>
      <xsl:attribute name="sample_id">
        <xsl:value-of select="@sample_id" />
      </xsl:attribute>
      <xsl:if test="@sample_item_id[. != '']">
        <xsl:attribute name="sample_item_id">
          <xsl:value-of select="@sample_item_id" />
        </xsl:attribute>
      </xsl:if>
      <xsl:attribute name="item_sequence">
        <xsl:value-of select="@item_sequence" />
      </xsl:attribute>
      <xsl:if test="@quantity[. != '']">
        <xsl:attribute name="quantity">
          <xsl:value-of select="@quantity" />
        </xsl:attribute>
      </xsl:if>
      <xsl:variable name="type_of_sample_id" select="@type_of_sample_id"/>
      <type_of_sample>
        <xsl:apply-templates select="//dictionary[@id = $type_of_sample_id]"/>
      </type_of_sample>
      <xsl:if test="@source_of_sample_id[. != '']">
        <xsl:variable name="source_of_sample_id" select="@source_of_sample_id"/>
        <source_of_sample id="{@source_of_sample_id}">
          <xsl:apply-templates select="//dictionary[@id = $source_of_sample_id]"/>
        </source_of_sample>
      </xsl:if>
      <xsl:if test="@source_other[. != '']">
        <source_other>
          <xsl:value-of select="source_other" />
        </source_other>
      </xsl:if>
      <xsl:if test="@container_id[. != '']">
        <xsl:variable name="container_id" select="@container_id"/>
        <container id="{@container_id}">
          <xsl:apply-templates select="//dictionary[@id = $container_id]"/>
        </container>
      </xsl:if>
      <xsl:if test="@unit_of_measure_id[. != '']">
        <xsl:variable name="unit_of_measure_id" select="@unit_of_measure_id"/>
        <unit_of_measure id="{@unit_of_measure_id}">
          <xsl:apply-templates select="//dictionary[@id = $unit_of_measure_id]"/>
        </unit_of_measure>
      </xsl:if>
      <xsl:variable name="sample_item_id" select="@id" />
      <!--
        The analyses for this sample item
      -->
      <xsl:apply-templates select="//analysis[@sample_item_id = $sample_item_id]" />
    </sample_item>
  </xsl:template>
  
  <xsl:template match="analysis">
    <analysis>
      <xsl:attribute name="id">
        <xsl:value-of select="@id" />
      </xsl:attribute>
      <xsl:attribute name="sample_item_id">
        <xsl:value-of select="@sample_item_id" />
      </xsl:attribute>
      <xsl:attribute name="revision">
        <xsl:value-of select="@revision" />
      </xsl:attribute>
      <xsl:if test="@pre_analysis_id[. != '']">
        <xsl:attribute name="pre_analysis_id">
          <xsl:value-of select="@pre_analysis_id" />
        </xsl:attribute>
      </xsl:if>
      <xsl:if test="@parent_result_id[. != '']">
        <xsl:attribute name="parent_result_id">
          <xsl:value-of select="@parent_result_id" />
        </xsl:attribute>
      </xsl:if>
      <xsl:attribute name="is_reportable">
        <xsl:value-of select="@is_reportable" />
      </xsl:attribute>
      <xsl:if test="@available_date[. != '']">
        <xsl:attribute name="available_date">
          <xsl:value-of select="@available_date" />
        </xsl:attribute>
      </xsl:if>
      <xsl:if test="@started_date[. != '']">
        <xsl:attribute name="started_date">
          <xsl:value-of select="@started_date" />
        </xsl:attribute>
      </xsl:if>
      <xsl:if test="@completed_date[. != '']">
        <xsl:attribute name="completed_date">
          <xsl:value-of select="@completed_date" />
        </xsl:attribute>
      </xsl:if>
      <xsl:if test="@released_date[. != '']">
        <xsl:attribute name="released_date">
          <xsl:value-of select="@released_date" />
        </xsl:attribute>
      </xsl:if>
      <xsl:if test="@printed_date[. != '']">
        <xsl:attribute name="printed_date">
          <xsl:value-of select="@printed_date" />
        </xsl:attribute>
      </xsl:if>
      <xsl:variable name="test_id" select="@test_id" />
      <test id="{@test_id}">
        <xsl:apply-templates select="//test[@id = $test_id]" />
      </test>
      <xsl:variable name="section_id" select="@section_id" />
      <section id="{@section_id}">
        <xsl:apply-templates select="//section[@id = $section_id]" />
      </section>
      <xsl:if test="@unit_of_measure_id[. != '']">
        <xsl:variable name="unit_of_measure_id" select="@unit_of_measure_id" />
        <unit_of_measure>
          <xsl:apply-templates select="//dictionary[@id = $unit_of_measure_id]" />
        </unit_of_measure>
      </xsl:if>
      <xsl:variable name="status_id" select="@status_id" />
      <status>
        <xsl:apply-templates select="//dictionary[@id = $status_id]" />
      </status>
      <xsl:variable name="analysis_id" select="@id" />
      <!--
        The results for this analysis
      -->
      <xsl:apply-templates select="//analysis_qaevent[@analysis_id = $analysis_id]" />
      <!-- 
        The results for this analysis
      -->
      <xsl:apply-templates select="//result[@analysis_id = $analysis_id]" />
      <!--
        The users associated with this analysis
      -->
      <xsl:apply-templates select="//analysis_user[@analysis_id = $analysis_id]" />
      <!-- 
        The notes for this analysis
      -->
      <xsl:apply-templates select="//analysis_external_notes/note[@reference_id = $analysis_id]" />
    </analysis>
  </xsl:template>
  
  <xsl:template match="test">
    <xsl:attribute name="id">
      <xsl:value-of select="@id"/>
    </xsl:attribute>
    <xsl:attribute name="is_active">
      <xsl:value-of select="@is_active"/>
    </xsl:attribute>
    <xsl:attribute name="active_begin">
      <xsl:value-of select="@active_begin"/>
    </xsl:attribute>
    <xsl:attribute name="active_end">
      <xsl:value-of select="@active_end"/>
    </xsl:attribute>
    <xsl:attribute name="is_reportable">
      <xsl:value-of select="@is_reportable"/>
    </xsl:attribute>
    <xsl:attribute name="time_transit">
      <xsl:value-of select="@time_transit"/>
    </xsl:attribute>
    <xsl:attribute name="time_holding">
      <xsl:value-of select="@time_holding"/>
    </xsl:attribute>
    <xsl:attribute name="time_ta_average">
      <xsl:value-of select="@time_ta_average"/>
    </xsl:attribute>
    <xsl:attribute name="time_ta_warning">
      <xsl:value-of select="@time_ta_warning"/>
    </xsl:attribute>    
    <xsl:attribute name="time_ta_max">
      <xsl:value-of select="@time_ta_max"/>
    </xsl:attribute>
    <xsl:if test="reporting_sequence[. != '']">
      <xsl:attribute name="reporting_sequence">
        <xsl:value-of select="@reporting_sequence"/>
      </xsl:attribute>
    </xsl:if>
    <name>
      <xsl:value-of select="name"/>
    </name>
    <description>
      <xsl:value-of select="description" />
    </description>
    <reporting_description>
      <xsl:value-of select="reporting_description" />
    </reporting_description>
    <xsl:variable name="method_id" select="@method_id" />
    <method>
      <xsl:apply-templates select="//method[@id = $method_id]" />
    </method>
    <xsl:variable name="test_format_id" select="@test_format_id" />
    <test_format>
      <xsl:apply-templates select="//dictionary[@id = $test_format_id]" />
    </test_format>      
    <xsl:if test="@revision_method_id[. != '']">
      <xsl:variable name="revision_method_id" select="@revision_method_id" />
      <revision_method>
        <xsl:apply-templates select="//dictionary[@id = $revision_method_id]" />
      </revision_method>
    </xsl:if>
    <xsl:variable name="reporting_method_id" select="@reporting_method_id" />
    <reporting_method>
      <xsl:apply-templates select="//dictionary[@id = $reporting_method_id]" />
    </reporting_method>
    <xsl:variable name="sorting_method_id" select="@sorting_method_id" />
    <sorting_method>
      <xsl:apply-templates select="//dictionary[@id = $sorting_method_id]" />
    </sorting_method>
    <xsl:if test="@test_trailer_id[. != '']">
      <xsl:variable name="test_trailer_id" select="@test_trailer_id" />
      <xsl:apply-templates select="//test_trailer[@id = $test_trailer_id]" />
    </xsl:if>
    <xsl:variable name="id" select="@id" />
    <xsl:apply-templates select="//test_translations/translation[@reference_id = $id]" />
  </xsl:template>
  
  <xsl:template match="method">
    <xsl:attribute name="id">
      <xsl:value-of select="@id" />
    </xsl:attribute>        
    <xsl:attribute name="is_active">
      <xsl:value-of select="@is_active" />
    </xsl:attribute>
    <xsl:attribute name="active_begin">
      <xsl:value-of select="@active_begin" />
    </xsl:attribute>
    <xsl:attribute name="active_end">
      <xsl:value-of select="@active_end" />
    </xsl:attribute>
    <name>
      <xsl:value-of select="name" />
    </name>
    <description>
      <xsl:value-of select="description" />
    </description>
    <reporting_description>
      <xsl:value-of select="reporting_description" />
    </reporting_description>
    <xsl:variable name="id" select="@id" />
    <xsl:apply-templates select="//method_translations/translation[@reference_id = $id]" />
  </xsl:template>
  
  <xsl:template match="section">
    <xsl:attribute name="id">
      <xsl:value-of select="@id" />
    </xsl:attribute>        
    <xsl:if test="@parent_section_id[. != '']">
      <xsl:attribute name="parent_section_id">
        <xsl:value-of select="@parent_section_id" />
      </xsl:attribute>
    </xsl:if>
    <xsl:attribute name="is_external">
      <xsl:value-of select="@is_external" />
    </xsl:attribute>
    <name>
      <xsl:value-of select="name" />
    </name>
    <description>
      <xsl:value-of select="description" />
    </description>  
    <xsl:variable name="organization_id" select="@organization_id" />
    <xsl:apply-templates select="//organization[@id = $organization_id]" />
  </xsl:template>
  
  <xsl:template match="test_trailer">
    <test_trailer>
      <xsl:attribute name="id">
        <xsl:value-of select="@id" />
      </xsl:attribute>
      <name>
        <xsl:value-of select="name" />
      </name>
      <description>
        <xsl:value-of select="description" />
      </description>
      <text>
        <xsl:value-of select="text" />
      </text>
    </test_trailer>
  </xsl:template>
  
  <xsl:template match="result">
    <result>
      <xsl:attribute name="id">
        <xsl:value-of select="@id" />
      </xsl:attribute>
      <xsl:attribute name="analysis_id">
        <xsl:value-of select="@analysis_id" />
      </xsl:attribute>
      <xsl:attribute name="test_analyte_id">
        <xsl:value-of select="@test_analyte_id" />
      </xsl:attribute>
      <xsl:if test="@test_result_id[. != '']">
        <xsl:attribute name="test_result_id">
          <xsl:value-of select="@test_result_id" />
        </xsl:attribute>
      </xsl:if>
      <xsl:attribute name="is_column">
        <xsl:value-of select="@is_column" />
      </xsl:attribute>
      <xsl:attribute name="sort_order">
        <xsl:value-of select="@sort_order" />
      </xsl:attribute>
      <xsl:attribute name="is_reportable">
        <xsl:value-of select="@is_reportable" />
      </xsl:attribute>
      <xsl:if test="@type_id[. != '']">
        <xsl:variable name="type_id" select="@type_id" />
         <!-- 
             The following element is called "Type" and not "type" because "type"
             is treated as a keyword by the module used for data exchange and is 
             not allowed to be used as the name of an attribute or element   
         --> 
        <Type>
          <xsl:apply-templates select="//dictionary[@id = $type_id]" />
        </Type>
      </xsl:if>
      <xsl:if test="value[. != '']">
        <value>
          <xsl:value-of select="value" />
        </value>
        <!-- 
            The link between this result and the dictionary entry that its value
            may be the id of  
         -->
        <xsl:variable name="id" select="@id" />
        <xsl:apply-templates select="//result_dictionary[@id = $id]" />
      </xsl:if>
      <xsl:variable name="analyte_id" select="@analyte_id" />
      <!--
        The analyte for this result
      -->
      <xsl:apply-templates select="//analyte[@id = $analyte_id]" />
    </result>
  </xsl:template>
  
  <xsl:template match="result_dictionary">    
     <!--
       The attribute "id" is not shown here even though it's in the original xml
       because it's not to be shown in the final xml 
     -->
    <dictionary>
      <xsl:variable name="dictionary_id" select="@dictionary_id" />
      <xsl:apply-templates select="//dictionary[@id = $dictionary_id]" />
    </dictionary> 
  </xsl:template>
  
  <xsl:template match="analysis_user">
    <analysis_user>
      <xsl:attribute name="id">
        <xsl:value-of select="@id" />
      </xsl:attribute>
      <xsl:attribute name="analysis_id">
        <xsl:value-of select="@analysis_id" />
      </xsl:attribute>
      <xsl:variable name="system_user_id" select="@system_user_id" />
      <system_user>
        <xsl:apply-templates select="//system_user[@id = $system_user_id]" />
      </system_user>
      <xsl:variable name="action_id" select="@action_id" />
      <action>
        <xsl:apply-templates select="//dictionary[@id = $action_id]" />
      </action>
    </analysis_user>
  </xsl:template>
  
  <xsl:template match="sample_project">
    <sample_project>
      <xsl:attribute name="id">
        <xsl:value-of select="@id" />
      </xsl:attribute>
      <xsl:attribute name="sample_id">
        <xsl:value-of select="@sample_id" />
      </xsl:attribute>
      <xsl:attribute name="is_permanent">
        <xsl:value-of select="@is_permanent" />
      </xsl:attribute>
      <xsl:variable name="project_id" select="@project_id" />
      <project>
        <xsl:apply-templates select="//project[@id = $project_id]" />
      </project>
    </sample_project>
  </xsl:template>
  
  <xsl:template match="project">
    <xsl:attribute name="id">
      <xsl:value-of select="@id" />
    </xsl:attribute>
    <xsl:attribute name="started_date">
      <xsl:value-of select="@started_date" />
    </xsl:attribute>
    <xsl:attribute name="completed_date">
      <xsl:value-of select="@completed_date" />
    </xsl:attribute>
    <xsl:attribute name="is_active">
      <xsl:value-of select="@is_active" />
    </xsl:attribute>
    <name>
      <xsl:value-of select="name" />
    </name>
    <description>
      <xsl:value-of select="description" />
    </description>
    <xsl:if test="reference_to[. != '']">
      <reference_to>
        <xsl:value-of select="reference_to" />
      </reference_to>
    </xsl:if>
    <xsl:if test="@owner_id[. != '']">
      <xsl:variable name="owner_id" select="@owner_id" />
      <owner>
        <xsl:apply-templates select="//system_user[@id = $owner_id]" />
      </owner>
    </xsl:if>
  </xsl:template>
  
  <xsl:template match="sample_organization">
    <sample_organization>
      <xsl:attribute name="id">
        <xsl:value-of select="@id" />
      </xsl:attribute>
      <xsl:attribute name="sample_id">
        <xsl:value-of select="@sample_id" />
      </xsl:attribute>
      <xsl:variable name="type_id" select="@type_id" />
      <!-- 
          The following element is called "Type" and not "type" because "type"
          is treated as a keyword by the module used for data exchange and is 
          not allowed to be used as the name of an attribute or element   
      --> 
      <Type>
        <xsl:apply-templates select="//dictionary[@id = $type_id]" />
      </Type>
      <xsl:variable name="organization_id" select="@organization_id" />
      <organization>
        <xsl:apply-templates select="//organization[@id = $organization_id]">
          <xsl:with-param name="attention">
            <xsl:value-of select="organization_attention" />
          </xsl:with-param>
        </xsl:apply-templates>
      </organization>
    </sample_organization>
  </xsl:template>
  
  <xsl:template match="organization">
    <xsl:param name="attention" />
    <xsl:attribute name="id">
      <xsl:value-of select="@id" />
    </xsl:attribute>
    <xsl:if test="@parent_organization_id[. != '']">
      <xsl:attribute name="parent_organization_id">
        <xsl:value-of select="@parent_organization_id" />
      </xsl:attribute>
    </xsl:if>
    <xsl:attribute name="is_active">
      <xsl:value-of select="@is_active" />
    </xsl:attribute>
    <name>
      <xsl:value-of select="name" />
    </name>
    <xsl:if test="$attention != ''">
      <attention>
        <xsl:value-of select="$attention" />
      </attention>
    </xsl:if>
    <xsl:variable name="address_id" select="@address_id" />
    <address>
      <xsl:apply-templates select="//address[@id = $address_id]" />
    </address>
    <xsl:variable name="id" select="@id" />
    <xsl:apply-templates select="//organization_translations/translation[@reference_id = $id]" />
  </xsl:template>
  
  <xsl:template match="address">
    <xsl:attribute name="id">
      <xsl:value-of select="@id" />
    </xsl:attribute>
    <xsl:if test="multiple_unit[. != '']">
      <multiple_unit>
        <xsl:value-of select="multiple_unit" />
      </multiple_unit>
    </xsl:if>
    <xsl:if test="street_address[. != '']">
      <street_address>
        <xsl:value-of select="street_address" />
      </street_address>
    </xsl:if>
    <xsl:if test="city[. != '']">
      <city>
        <xsl:value-of select="city" />
      </city>
    </xsl:if>
    <xsl:if test="state[. != '']">
      <state>
        <xsl:value-of select="state" />
      </state>
    </xsl:if>
    <xsl:if test="zip_code[. != '']">
      <zip_code>
        <xsl:value-of select="zip_code" />
      </zip_code>
    </xsl:if>
    <xsl:if test="work_phone[. != '']">
      <work_phone>
        <xsl:value-of select="zip_code" />
      </work_phone>
    </xsl:if>
    <xsl:if test="home_phone[. != '']">
      <home_phone>
        <xsl:value-of select="home_phone" />
      </home_phone>
    </xsl:if>
    <xsl:if test="cell_phone[. != '']">
      <cell_phone>
        <xsl:value-of select="cell_phone" />
      </cell_phone>
    </xsl:if>
    <xsl:if test="fax_phone[. != '']">
      <fax_phone>
        <xsl:value-of select="fax_phone" />
      </fax_phone>
    </xsl:if>
    <xsl:if test="email[. != '']">
      <email>
        <xsl:value-of select="email" />
      </email>
    </xsl:if>
    <xsl:if test="country[. != '']">
      <country>
        <xsl:value-of select="country" />
      </country>
    </xsl:if>
  </xsl:template>
  
  <xsl:template match="sample_qaevent">
    <sample_qaevent>
      <xsl:attribute name="id">
        <xsl:value-of select="@id" />
      </xsl:attribute>
      <xsl:attribute name="sample_id">
        <xsl:value-of select="@sample_id" />
      </xsl:attribute>
      <xsl:attribute name="is_billable">
        <xsl:value-of select="@is_billable" />
      </xsl:attribute>
      <xsl:variable name="qaevent_id" select="@qaevent_id" />
      <xsl:apply-templates select="//qaevent[@id = $qaevent_id]" />
      <xsl:variable name="type_id" select="@type_id" />
      <!-- 
          The following element is called "Type" and not "type" because "type"
          is treated as a keyword by the module used for data exchange and is 
          not allowed to be used as the name of an attribute or element   
      -->
      <Type>
        <xsl:apply-templates select="//dictionary[@id = $type_id]" />
      </Type>
    </sample_qaevent>
  </xsl:template>
  
  <xsl:template match="analysis_qaevent">
    <analysis_qaevent>
      <xsl:attribute name="id">
        <xsl:value-of select="@id" />
      </xsl:attribute>
      <xsl:attribute name="analysis_id">
        <xsl:value-of select="@analysis_id" />
      </xsl:attribute>
      <xsl:attribute name="is_billable">
        <xsl:value-of select="@is_billable" />
      </xsl:attribute>
      <xsl:variable name="qaevent_id" select="@qaevent_id" />
      <xsl:apply-templates select="//qaevent[@id = $qaevent_id]" />
      <xsl:variable name="type_id" select="@type_id" />
      <!-- 
          The following element is called "Type" and not "type" because "type"
          is treated as a keyword by the module used for data exchange and is 
          not allowed to be used as the name of an attribute or element   
      -->
      <Type>
        <xsl:apply-templates select="//dictionary[@id = $type_id]" />
      </Type>
    </analysis_qaevent>
  </xsl:template>
  
  <xsl:template match="qaevent">
    <qaevent>
      <xsl:attribute name="id">
        <xsl:value-of select="@id" />
      </xsl:attribute>
      <xsl:attribute name="is_billable">
        <xsl:value-of select="@is_billable" />
      </xsl:attribute>
      <xsl:if test="@reporting_sequence[. != '']">
        <xsl:attribute name="reporting_sequence">
          <xsl:value-of select="@reporting_sequence" />
        </xsl:attribute>
      </xsl:if>
      <name>
        <xsl:value-of select="name" />
      </name>
      <xsl:if test="description[. != '']">
        <description>
          <xsl:value-of select="description" />
        </description>
      </xsl:if>
      <xsl:if test="test_id[. != '']">
        <xsl:variable name="test_id" select="@test_id" />
        <test id="{@test_id}">
          <xsl:apply-templates select="//test[@id = $test_id]" />
        </test>
      </xsl:if>
      <xsl:variable name="type_id" select="@type_id" />
      <!-- 
          The following element is called "Type" and not "type" because "type"
          is treated as a keyword by the module used for data exchange and is 
          not allowed to be used as the name of an attribute or element   
      -->
      <Type>
        <xsl:apply-templates select="//dictionary[@id = $type_id]" />
      </Type>
      <reporting_text>
        <xsl:value-of select="reporting_text" />
      </reporting_text>
    </qaevent>
  </xsl:template>
  
  <xsl:template match="aux_data">
    <aux_data>
      <xsl:attribute name="id">
        <xsl:value-of select="@id" />
      </xsl:attribute>
      <xsl:attribute name="sort_order">
        <xsl:value-of select="@sort_order" />
      </xsl:attribute>
      <xsl:attribute name="aux_field_id">
        <xsl:value-of select="@aux_field_id" />
      </xsl:attribute>
      <xsl:attribute name="reference_id">
        <xsl:value-of select="@reference_id" />
      </xsl:attribute>
      <xsl:attribute name="reference_table_id">
        <xsl:value-of select="@reference_table_id" />
      </xsl:attribute>
      <xsl:attribute name="is_reportable">
        <xsl:value-of select="@is_reportable" />
      </xsl:attribute>
      <xsl:variable name="type_id" select="@type_id" />
      <!-- 
          The following element is called "Type" and not "type" because "type"
          is treated as a keyword by the module used for data exchange and is 
          not allowed to be used as the name of an attribute or element   
      -->
      <Type>
        <xsl:apply-templates select="//dictionary[@id = $type_id]" />
      </Type>
      <xsl:if test="value[. != '']">
        <value>
          <xsl:value-of select="value" />
        </value>
        <!-- 
            The link between this aux data and the dictionary entry that its 
            value may be the id of  
         -->
        <xsl:variable name="id" select="@id" />
        <xsl:apply-templates select="//aux_data_dictionary[@id = $id]" />
      </xsl:if>
      <xsl:variable name="analyte_id" select="@analyte_id" />
      <!--
        The analyte for this aux data
      -->
      <xsl:apply-templates select="//analyte[@id = $analyte_id]" />
    </aux_data>
  </xsl:template>
  
  <xsl:template match="aux_data_dictionary">
    <!--
       The attribute "id" is not shown here even though it's in the original xml
       because it's not to be shown in the final xml 
     -->  
    <dictionary>
      <xsl:variable name="dictionary_id" select="@dictionary_id" />
      <xsl:apply-templates select="//dictionary[@id = $dictionary_id]" />
    </dictionary> 
  </xsl:template>
  
  <xsl:template match="analyte">
    <analyte>
      <xsl:attribute name="id">
        <xsl:value-of select="@id" />
      </xsl:attribute>
      <xsl:attribute name="is_active">
        <xsl:value-of select="@is_active" />
      </xsl:attribute>
      <xsl:if test="parent_analyte_id[. != '']">
        <xsl:attribute name="parent_analyte_id">
          <xsl:value-of select="@parent_analyte_id" />
        </xsl:attribute>
      </xsl:if>
      <name>
        <xsl:value-of select="name" />
      </name>
      <xsl:if test="external_id[. != '']">
        <external_id>
          <xsl:value-of select="external_id" />
        </external_id>
      </xsl:if>
      <xsl:variable name="id" select="@id" />
      <xsl:apply-templates select="//analyte_translations/translation[@reference_id = $id]" />
    </analyte>
  </xsl:template>
  
  <xsl:template match="dictionary">
    <xsl:attribute name="id">
      <xsl:value-of select="@id" />
    </xsl:attribute>
    <xsl:if test="system_name[. != '']">
      <system_name>
        <xsl:value-of select="system_name" />
      </system_name>
    </xsl:if>
    <entry>
      <xsl:value-of select="entry" />
    </entry>
    <xsl:variable name="id" select="@id" />
    <xsl:apply-templates select="//dictionary_translations/translation[@reference_id = $id]" />
  </xsl:template>
  
  <xsl:template match="system_user">
    <xsl:attribute name="id">
      <xsl:value-of select="@id" />
    </xsl:attribute>
    <xsl:if test="external_id[. != '']">
      <external_id>
        <xsl:value-of select="external_id" />
      </external_id>
    </xsl:if>
    <login_name>
      <xsl:value-of select="login_name" />
    </login_name>
    <xsl:if test="last_name[.!= '']">
      <last_name>
        <xsl:value-of select="last_name" />
      </last_name>
    </xsl:if>
    <xsl:if test="first_name[. != '']">
      <first_name>
        <xsl:value-of select="first_name" />
      </first_name>
    </xsl:if>
    <xsl:if test="initials[. != '']">
      <initials>
        <xsl:value-of select="initials" />
      </initials>
    </xsl:if>
  </xsl:template>
  
  <xsl:template match="sample_external_notes">
    <xsl:apply-templates select="//note" />
  </xsl:template>
  
  <xsl:template match="analysis_external_notes">
    <xsl:apply-templates select="//note" />
  </xsl:template>
  
  <xsl:template match="note">
    <note>
      <xsl:attribute name="id">
        <xsl:value-of select="@id" />
      </xsl:attribute>
      <xsl:attribute name="reference_id">
        <xsl:value-of select="@reference_id" />
      </xsl:attribute>
      <xsl:attribute name="reference_table_id">
        <xsl:value-of select="@reference_table_id" />
      </xsl:attribute>
      <xsl:attribute name="timestamp">
        <xsl:value-of select="@timestamp"/>
      </xsl:attribute>
      <xsl:attribute name="is_external">
        <xsl:value-of select="@is_external" />
      </xsl:attribute>
      <xsl:variable name="system_user_id" select="@system_user_id" />
      <system_user>
        <xsl:apply-templates select="//system_user[@id = $system_user_id]" />
      </system_user>
      <xsl:if test="subject[.!= '']">
        <subject>
          <xsl:value-of select="subject" />
        </subject>
      </xsl:if>
      <xsl:if test="text[.!= '']">
        <text>
          <xsl:value-of select="text" />
        </text>
      </xsl:if>
    </note>
  </xsl:template>
  
  <xsl:template match="dictionary_translations">
    <xsl:apply-templates select="//translation" />
  </xsl:template>
  
  <xsl:template match="test_translations">
    <xsl:apply-templates select="//translation" />
  </xsl:template>
  
  <xsl:template match="method_translations">
    <xsl:apply-templates select="//translation" />
  </xsl:template>
  
  <xsl:template match="analyte_translations">
    <xsl:apply-templates select="//translation" />
  </xsl:template>
  
  <xsl:template match="organization_translations">
    <xsl:apply-templates select="//translation" />
  </xsl:template>
  
  <xsl:template match="translation">
    <translation>
      <xsl:attribute name="id">
        <xsl:value-of select="@id" />
      </xsl:attribute>
      <xsl:attribute name="reference_id">
        <xsl:value-of select="@reference_id" />
      </xsl:attribute>
      <xsl:attribute name="is_active">
        <xsl:value-of select="@is_active" />
      </xsl:attribute>
      <profile>
        <xsl:value-of select="profile" />
      </profile>
      <code>
        <xsl:value-of select="code" />
      </code>
      <description>
        <xsl:value-of select="description" />
      </description>
      <coding_system>
        <xsl:value-of select="coding_system" />
      </coding_system>
      <xsl:if test="version[.!= '']">
        <version>
          <xsl:value-of select="version" />
        </version>
      </xsl:if>
    </translation>
  </xsl:template>
</xsl:stylesheet>