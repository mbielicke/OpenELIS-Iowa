<!-- Exhibit A - UIRF Open-source Based Public Software License. The contents 
	of this file are subject to the UIRF Open-source Based Public Software License(the 
	"License"); you may not use this file except in compliance with the License. 
	You may obtain a copy of the License at openelis.uhl.uiowa.edu Software distributed 
	under the License is distributed on an "AS IS" basis, WITHOUT WARRANTY OF 
	ANY KIND, either express or implied. See the License for the specific language 
	governing rights and limitations under the License. The Original Code is 
	OpenELIS code. The Initial Developer of the Original Code is The University 
	of Iowa. Portions created by The University of Iowa are Copyright 2006-2008. 
	All Rights Reserved. Contributor(s): ______________________________________. 
	Alternatively, the contents of this file marked "Separately-Licensed" may 
	be used under the terms of a UIRF Software license ("UIRF Software License"), 
	in which case the provisions of a UIRF Software License are applicable instead 
	of those above. -->
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" indent="yes" />

	<!-- ****** message is root ****** -->

	<xsl:template match="message">
		<xsl:copy>
			<xsl:copy-of select="@*" />
			<xsl:apply-templates select="header" />
			<xsl:apply-templates select="sample" />
		</xsl:copy>
	</xsl:template>

	<!-- ****** level 1: sample ****** -->

	<xsl:template match="sample">
		<xsl:variable name="id" select="@id" />
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />

			<xsl:apply-templates select="//system_user[@id = current()/@received_by_id]">
				<xsl:with-param name="tagname">received_by</xsl:with-param>
			</xsl:apply-templates>

			<xsl:apply-templates select="//dictionary[@id = current()/@status_id]">
				<xsl:with-param name="tagname">status</xsl:with-param>
			</xsl:apply-templates>

			<!-- electronic order information -->
			 <xsl:apply-templates select="//eorder[@id = current()/@order_id]" /> 

			<!-- domain information -->
			<xsl:apply-templates select="//sample_environmental[@sample_id = $id]" />
			<xsl:apply-templates select="//sample_private_well[@sample_id = $id]" />
			<xsl:apply-templates select="//sample_sdwis[@sample_id = $id]" />
			<xsl:apply-templates select="//sample_neonatal[@sample_id = $id]" />
			<xsl:apply-templates select="//sample_clinical[@sample_id = $id]" />
			<xsl:apply-templates select="//sample_animal[@sample_id = $id]" />
			<xsl:apply-templates select="//sample_pt[@sample_id = $id]" />

			<!-- project, organization, ... -->
			<xsl:apply-templates select="//sample_project[@sample_id = $id]" />
			<xsl:apply-templates select="//sample_organization[@sample_id = $id]" />
			<xsl:apply-templates
				select="//sample_external_notes/note[@reference_id = $id]" />
			<xsl:apply-templates select="//sample_qaevent[@sample_id = $id]" />
			<xsl:apply-templates select="//aux_data[@reference_id = $id]" />

			<!-- level 2 data -->
			<xsl:apply-templates select="//sample_item[@sample_id = $id]" />
		</xsl:copy>
	</xsl:template>

	<!-- sample related maps -->

	<xsl:template match="sample_environmental">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />

			<xsl:apply-templates
				select="//address[@id = current()/@location_address_id]">
				<xsl:with-param name="tagname">location_address</xsl:with-param>
			</xsl:apply-templates>

		</xsl:copy>
	</xsl:template>

	<xsl:template match="sample_private_well">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />

			<xsl:apply-templates
				select="//address[@id = current()/@location_address_id]">
				<xsl:with-param name="tagname">location_address</xsl:with-param>
			</xsl:apply-templates>

		</xsl:copy>
	</xsl:template>

	<xsl:template match="sample_sdwis">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />

			<xsl:apply-templates select="//pws[@id = current()/@pws_id]" />

			<xsl:apply-templates select="//dictionary[@id = current()/@sample_type_id]">
				<xsl:with-param name="tagname">sample_type</xsl:with-param>
			</xsl:apply-templates>

			<xsl:apply-templates
				select="//dictionary[@id = current()/@sample_category_id]">
				<xsl:with-param name="tagname">sample_category</xsl:with-param>
			</xsl:apply-templates>

		</xsl:copy>
	</xsl:template>

	<xsl:template match="sample_neonatal">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />

			<xsl:apply-templates select="//dictionary[@id = current()/@feeding_id]">
				<xsl:with-param name="tagname">feeding</xsl:with-param>
			</xsl:apply-templates>

			<xsl:apply-templates select="//patient[@id = current()/@patient_id]">
				<xsl:with-param name="tagname">patient</xsl:with-param>
			</xsl:apply-templates>

			<xsl:apply-templates select="//patient[@id = current()/@next_of_kin_id]">
				<xsl:with-param name="tagname">next_of_kin</xsl:with-param>
				<xsl:with-param name="relation_id"><xsl:value-of select="@next_of_kin_relation_id" /></xsl:with-param>
			</xsl:apply-templates>

			<xsl:apply-templates select="//provider[@id = current()/@provider_id]" />

		</xsl:copy>
	</xsl:template>

	<xsl:template match="sample_clinical">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />

			<xsl:apply-templates select="//patient[@id = current()/@patient_id]">
				<xsl:with-param name="tagname">patient</xsl:with-param>
			</xsl:apply-templates>

			<xsl:apply-templates select="//provider[@id = current()/@provider_id]" />

		</xsl:copy>
	</xsl:template>

	<xsl:template match="sample_project">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />

			<xsl:apply-templates select="//project[@id = current()/@project_id]" />

		</xsl:copy>
	</xsl:template>

	<xsl:template match="project">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />

			<xsl:apply-templates select="//system_user[@id = current()/@owner_id]">
				<xsl:with-param name="tagname">owner</xsl:with-param>
			</xsl:apply-templates>

		</xsl:copy>
	</xsl:template>

	<xsl:template match="sample_organization">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />

			<xsl:apply-templates select="//dictionary[@id = current()/@type_id]">
				<xsl:with-param name="tagname">Type</xsl:with-param>
			</xsl:apply-templates>

			<xsl:apply-templates
				select="//organization[@id = current()/@organization_id]">
				<xsl:with-param name="attention">	<xsl:value-of select="organization_attention" /></xsl:with-param>
			</xsl:apply-templates>

		</xsl:copy>
	</xsl:template>

	<xsl:template match="organization">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />

			<xsl:apply-templates select="//address[@id = current()/@address_id]" />
			<xsl:apply-templates
				select="//organization_translations/translation[@reference_id = current()/@id]" />
		</xsl:copy>
	</xsl:template>

	<!-- used in the header for destination URL etc. -->
	<xsl:template match="organization_parameter">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />
			<xsl:apply-templates select="//dictionary[@id = current()/@type_id]">
				<xsl:with-param name="tagname">Type</xsl:with-param>
			</xsl:apply-templates>

		</xsl:copy>
	</xsl:template>

	<xsl:template match="sample_qaevent">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />

			<xsl:apply-templates select="//qaevent[@id = current()/@qaevent_id]" />

			<xsl:apply-templates select="//dictionary[@id = current()/@type_id]">
				<xsl:with-param name="tagname">Type</xsl:with-param>
			</xsl:apply-templates>

		</xsl:copy>
	</xsl:template>

	<xsl:template match="aux_data">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />

			<xsl:apply-templates select="//analyte[@id = current()/@analyte_id]">
				<xsl:with-param name="test-analyte-id"></xsl:with-param>
			</xsl:apply-templates>

			<xsl:apply-templates select="//dictionary[@id = current()/@type_id]">
				<xsl:with-param name="tagname">Type</xsl:with-param>
			</xsl:apply-templates>

			<xsl:apply-templates select="//aux_data_dictionary[@id = current()/@id]" />

		</xsl:copy>
	</xsl:template>

	<xsl:template match="aux_data_dictionary">
		<xsl:apply-templates select="//dictionary[@id = current()/@dictionary_id]">
			<xsl:with-param name="tagname">dictionary</xsl:with-param>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="patient">
		<xsl:param name="tagname" />
		<xsl:param name="relation_id" />
		<xsl:element name="{$tagname}">
			<xsl:apply-templates select="@*|node()" />

			<xsl:apply-templates select="//address[@id = current()/@address_id]" />

			<xsl:apply-templates select="//dictionary[@id = current()/@gender_id]">
				<xsl:with-param name="tagname">gender</xsl:with-param>
			</xsl:apply-templates>

			<xsl:apply-templates select="//dictionary[@id = current()/@race_id]">
				<xsl:with-param name="tagname">race</xsl:with-param>
			</xsl:apply-templates>

			<xsl:apply-templates select="//dictionary[@id = current()/@ethnicity_id]">
				<xsl:with-param name="tagname">ethnicity</xsl:with-param>
			</xsl:apply-templates>

			<xsl:if test="$relation_id != ''">
				<xsl:apply-templates select="//dictionary[@id = $relation_id]">
					<xsl:with-param name="tagname">relation</xsl:with-param>
				</xsl:apply-templates>
			</xsl:if>

		</xsl:element>
	</xsl:template>

	<xsl:template match="provider">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />

			<xsl:apply-templates select="//dictionary[@id = current()/@type_id]">
				<xsl:with-param name="tagname">Type</xsl:with-param>
			</xsl:apply-templates>
		</xsl:copy>
	</xsl:template>

	<xsl:template match="eorder">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />

			<xsl:apply-templates select="//eorder_link[@eorder_id = current()/@id]" />
		</xsl:copy>
	</xsl:template>

	<xsl:template match="eorder_link">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />
		</xsl:copy>
	</xsl:template>

	<!-- ******** level 2: sample items ******** -->

	<xsl:template match="sample_item">
		<xsl:copy>
			<xsl:copy-of select="@*" />
			<xsl:apply-templates select="@*|node()" />
			<xsl:apply-templates
				select="//dictionary[@id = current()/@type_of_sample_id]">
				<xsl:with-param name="tagname">type_of_sample</xsl:with-param>
			</xsl:apply-templates>
			<xsl:apply-templates
				select="//dictionary[@id = current()/@source_of_sample_id]">
				<xsl:with-param name="tagname">source_of_sample</xsl:with-param>
			</xsl:apply-templates>
			<xsl:apply-templates select="//dictionary[@id = current()/@container_id]">
				<xsl:with-param name="tagname">container</xsl:with-param>
			</xsl:apply-templates>
			<xsl:apply-templates
				select="//dictionary[@id = current()/@unit_of_measure_id]">
				<xsl:with-param name="tagname">unit_of_measure</xsl:with-param>
			</xsl:apply-templates>
			<xsl:apply-templates select="//analysis[@sample_item_id = current()/@id]" />
		</xsl:copy>
	</xsl:template>

	<!-- ************ level 3: analysis ********* -->

	<xsl:template match="analysis">
		<xsl:variable name="id" select="@id" />
		<xsl:copy>
			<xsl:copy-of select="@*" />
			<xsl:apply-templates select="@*|node()" />
			<xsl:apply-templates select="//test[@id = current()/@test_id]" />
			<xsl:apply-templates select="//section[@id = current()/@section_id]" />
			<xsl:apply-templates
				select="//dictionary[@id = current()/@unit_of_measure_id]">
				<xsl:with-param name="tagname">unit_of_measure</xsl:with-param>
			</xsl:apply-templates>
			<xsl:apply-templates select="//dictionary[@id = current()/@status_id]">
				<xsl:with-param name="tagname">status</xsl:with-param>
			</xsl:apply-templates>
			<xsl:apply-templates select="//dictionary[@id = current()/@type_id]">
				<xsl:with-param name="tagname">Type</xsl:with-param>
			</xsl:apply-templates>
			<xsl:apply-templates select="//panel[@id = current()/@panel_id]" />
			<xsl:apply-templates select="//analysis_qaevent[@analysis_id = $id]" />
			<xsl:apply-templates select="//analysis_user[@analysis_id = $id]" />
			<xsl:apply-templates
				select="//analysis_external_notes/note[@reference_id = $id]" />
			<xsl:apply-templates select="//result[@analysis_id = $id]" />
			<xsl:apply-templates select="//analysis_qc[@analysis_id = $id]" />
		</xsl:copy>
	</xsl:template>

	<xsl:template match="analysis_qaevent">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />

			<xsl:apply-templates select="//qaevent[@id = current()/@qaevent_id]" />

			<xsl:apply-templates select="//dictionary[@id = current()/@type_id]">
				<xsl:with-param name="tagname">Type</xsl:with-param>
			</xsl:apply-templates>

		</xsl:copy>
	</xsl:template>

	<xsl:template match="analysis_user">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />

			<xsl:apply-templates select="//system_user[@id = current()/@system_user_id]">
				<xsl:with-param name="tagname">system_user</xsl:with-param>
			</xsl:apply-templates>

			<xsl:apply-templates select="//dictionary[@id = current()/@action_id]">
				<xsl:with-param name="tagname">action</xsl:with-param>
			</xsl:apply-templates>

		</xsl:copy>
	</xsl:template>

	<xsl:template match="test">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />

			<xsl:apply-templates select="//method[@id = current()/@method_id]" />

			<xsl:apply-templates select="//dictionary[@id = current()/@test_format_id]">
				<xsl:with-param name="tagname">test_format</xsl:with-param>
			</xsl:apply-templates>

			<xsl:apply-templates
				select="//dictionary[@id = current()/@revision_method_id]">
				<xsl:with-param name="tagname">revision_method</xsl:with-param>
			</xsl:apply-templates>

			<xsl:apply-templates
				select="//dictionary[@id = current()/@reporting_method_id]">
				<xsl:with-param name="tagname">reporting_method</xsl:with-param>
			</xsl:apply-templates>

			<xsl:apply-templates
				select="//dictionary[@id = current()/@sorting_method_id]">
				<xsl:with-param name="tagname">sorting_method</xsl:with-param>
			</xsl:apply-templates>

			<xsl:apply-templates
				select="//test_trailer[@id = current()/@test_trailer_id]" />

			<xsl:apply-templates
				select="//test_translations/translation[@reference_id = current()/@id]" />

		</xsl:copy>
	</xsl:template>

	<xsl:template match="method">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />

			<xsl:apply-templates
				select="//method_translations/translation[@reference_id = current()/@id]" />

		</xsl:copy>
	</xsl:template>
  
    <xsl:template match="section">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()" />

            <xsl:apply-templates
                select="//organization_translations/translation[@organization_id = current()/@id]" />

        </xsl:copy>
    </xsl:template>

	<xsl:template match="panel">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />

			<xsl:apply-templates
				select="//panel_translations/translation[@reference_id = current()/@id]" />

		</xsl:copy>
	</xsl:template>

	<!-- ************ level 4: result ********* -->

	<xsl:template match="result">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />

			<xsl:apply-templates select="//test_result[@id = current()/@test_result_id]" />

			<xsl:apply-templates select="//analyte[@id = current()/@analyte_id]">
				<xsl:with-param name="test-analyte-id"><xsl:value-of select="current()/@test_analyte_id" /></xsl:with-param>
			</xsl:apply-templates>

			<xsl:apply-templates select="//dictionary[@id = current()/@type_id]">
				<xsl:with-param name="tagname">Type</xsl:with-param>
			</xsl:apply-templates>

			<xsl:apply-templates select="//result_dictionary[@id = current()/@id]" />

		</xsl:copy>
	</xsl:template>

	<xsl:template match="result_dictionary">
		<xsl:apply-templates select="//dictionary[@id = current()/@dictionary_id]">
			<xsl:with-param name="tagname">dictionary</xsl:with-param>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="analysis_qc">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />

			<xsl:apply-templates
				select="//qc_result[@worksheet_qc_result_id = current()/@id]" />

			<xsl:apply-templates select="//dictionary[@id = current()/@qc_type_id]">
				<xsl:with-param name="tagname">qc_type</xsl:with-param>
			</xsl:apply-templates>

			<xsl:apply-templates select="//dictionary[@id = current()/@location_id]">
				<xsl:with-param name="tagname">location</xsl:with-param>
			</xsl:apply-templates>

			<xsl:apply-templates
				select="//dictionary[@id = current()/@prepared_unit_id]">
				<xsl:with-param name="tagname">prepared_unit</xsl:with-param>
			</xsl:apply-templates>

			<xsl:apply-templates select="//system_user[@id = current()/@prepared_by_id]">
				<xsl:with-param name="tagname">prepared_by</xsl:with-param>
			</xsl:apply-templates>

			<xsl:apply-templates select="//analyte[@id = current()/@analyte_id]" />

			<xsl:apply-templates
				select="//dictionary[@id = current()/@qc_analyte_type_id]">
				<xsl:with-param name="tagname">qc_analyte_type</xsl:with-param>
			</xsl:apply-templates>

		</xsl:copy>
	</xsl:template>

	<xsl:template match="qc_result">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />
		</xsl:copy>
	</xsl:template>

	<!-- ************ general ********* -->

	<xsl:template match="qaevent">
		<xsl:copy>
			<xsl:apply-templates select="node()" />
		</xsl:copy>
	</xsl:template>

	<xsl:template match="note">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />

			<xsl:apply-templates select="//system_user[@id = current()/@system_user_id]">
				<xsl:with-param name="tagname">system_user</xsl:with-param>
			</xsl:apply-templates>

		</xsl:copy>
	</xsl:template>

	<xsl:template match="system_user">
		<xsl:param name="tagname" />
		<xsl:element name="{$tagname}">
			<xsl:apply-templates select="@*|node()" />
		</xsl:element>
	</xsl:template>

	<xsl:template match="dictionary">
		<xsl:param name="tagname" />
		<xsl:element name="{$tagname}">
			<xsl:apply-templates select="@*|node()" />
			<xsl:apply-templates
				select="//dictionary_translations/translation[@reference_id = current()/@id]" />
		</xsl:element>
	</xsl:template>

	<xsl:template match="analyte">
		<xsl:param name="test-analyte-id" />
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />
			<xsl:apply-templates
				select="//test_analyte_translations/translation[@reference_id = $test-analyte-id]" />
			<xsl:if
				test="not (//test_analyte_translations/translation[@reference_id = $test-analyte-id])">
				<xsl:apply-templates
					select="//analyte_translations/translation[@reference_id = current()/@id]" />
			</xsl:if>
		</xsl:copy>
	</xsl:template>

	<xsl:template match="test_result">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />

			<xsl:apply-templates select="//dictionary[@id = current()/@flags_id]">
				<xsl:with-param name="tagname">flags</xsl:with-param>
			</xsl:apply-templates>
		</xsl:copy>
	</xsl:template>

	<xsl:template match="address">
		<xsl:param name="tagname" />

		<xsl:if test="$tagname != ''">
			<xsl:element name="{$tagname}">
				<xsl:apply-templates select="@*|node()" />
			</xsl:element>
		</xsl:if>

		<xsl:if test="not ($tagname)">
			<xsl:copy>
				<xsl:copy-of select="@*" />
				<xsl:apply-templates select="@*|node()" />
			</xsl:copy>
		</xsl:if>
	</xsl:template>

	<!-- copy all nodes and attributes -->

	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />
		</xsl:copy>
	</xsl:template>

</xsl:stylesheet>