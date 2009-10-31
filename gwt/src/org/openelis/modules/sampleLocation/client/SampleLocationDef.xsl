	<!--
		Exhibit A - UIRF Open-source Based Public Software License. The
		contents of this file are subject to the UIRF Open-source Based Public
		Software License(the "License"); you may not use this file except in
		compliance with the License. You may obtain a copy of the License at
		openelis.uhl.uiowa.edu Software distributed under the License is
		distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either
		express or implied. See the License for the specific language
		governing rights and limitations under the License. The Original Code
		is OpenELIS code. The Initial Developer of the Original Code is The
		University of Iowa. Portions created by The University of Iowa are
		Copyright 2006-2008. All Rights Reserved. Contributor(s):
		______________________________________. Alternatively, the contents of
		this file marked "Separately-Licensed" may be used under the terms of
		a UIRF Software license ("UIRF Software License"), in which case the
		provisions of a UIRF Software License are applicable instead of those
		above.
	-->
<xsl:stylesheet
  version="1.0"
  extension-element-prefixes="resource"
  xmlns:locale="xalan://java.util.Locale"
  xmlns:resource="xalan://org.openelis.util.UTFResource"
  xmlns:xalan="http://xml.apache.org/xalan"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xsi:noNamespaceSchemaLocation="http://openelis.uhl.uiowa.edu/schema/ScreenSchema.xsd"
  xsi:schemaLocation="http://www.w3.org/1999/XSL/Transform http://openelis.uhl.uiowa.edu/schema/XSLTSchema.xsd"
  xmlns:addressMeta="xalan://org.openelis.meta.AddressMeta" 
  xmlns:envMeta="xalan://org.openelis.metamap.SampleEnvironmentalMetaMap" >
	<xsl:import href="IMPORT/button.xsl" />

	<xalan:component prefix="resource">
		<xalan:script lang="javaclass"
			src="xalan://org.openelis.util.UTFResource" />
	</xalan:component>

	<xalan:component prefix="locale">
		<xalan:script lang="javaclass" src="xalan://java.util.Locale" />
	</xalan:component>
	
	<xalan:component prefix="envMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.SampleEnvironmentalMetaMap"/>
  	</xalan:component>
  
  	<xalan:component prefix="addressMeta">
    	<xalan:script lang="javaclass" src="xalan://org.openelis.meta.AddressMeta"/>
  	</xalan:component>
	
	<xsl:template match="doc">
		<xsl:variable name="env" select="envMeta:new()"/>
	    <xsl:variable name="address" select="envMeta:getAddress($env)"/>
		<xsl:variable name="language">
		<xsl:value-of select="locale" />
		</xsl:variable>
		<xsl:variable name="props">
			<xsl:value-of select="props" />
		</xsl:variable>
		<xsl:variable name="constants"
			select="resource:getBundle(string($props),locale:new(string($language)))" />
		<screen id="SampleLocationPicker" name="{resource:getString($constants,'sampleLocation')}" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
				<VerticalPanel spacing="0" padding="0">
					<VerticalPanel style="WhiteContentPanel" spacing="0" padding="0" width="300px">
						<TablePanel style="Form">
					<row>
					<text style="Prompt"><xsl:value-of select='resource:getString($constants,"location")'/>:</text>
						<widget colspan="3">
							<textbox key="{envMeta:getSamplingLocation($env)}" width="214px" max="30" tab="{addressMeta:getMultipleUnit($address)},{addressMeta:getCountry($address)}" field="String"/>
						</widget>
					</row>
					<row>
						<text style="Prompt"><xsl:value-of select='resource:getString($constants,"aptSuite")'/>:</text>
						<widget colspan="3">
							<textbox case="UPPER" key="{addressMeta:getMultipleUnit($address)}" width="214px" max="30" tab="{addressMeta:getStreetAddress($address)},{envMeta:getSamplingLocation($env)}" field="String"/>
						</widget>		
					</row>
					<row>
						<text style="Prompt"><xsl:value-of select='resource:getString($constants,"address")'/>:</text>
						<widget colspan="3">
							<textbox case="UPPER" key="{addressMeta:getStreetAddress($address)}" width="214px" max="30" tab="{addressMeta:getCity($address)},{addressMeta:getMultipleUnit($address)}" field="String"/>
						</widget>		
					</row>
					<row>
						<text style="Prompt"><xsl:value-of select='resource:getString($constants,"city")'/>:</text>
						<widget colspan="3">
							<textbox case="UPPER" key="{addressMeta:getCity($address)}" width="214px" max="30" tab="{addressMeta:getState($address)},{addressMeta:getStreetAddress($address)}" field="String"/>
						</widget>		
					</row>
					<row>
						<text style="Prompt"><xsl:value-of select='resource:getString($constants,"state")'/>:</text>
						<widget>
							<dropdown case="UPPER" key="{addressMeta:getState($address)}" width="40px" tab="{addressMeta:getZipCode($address)},{addressMeta:getCity($address)}" field="String"/>
						</widget>
						
						<text style="Prompt"><xsl:value-of select='resource:getString($constants,"zipcode")'/>:</text>
						<widget>
							<textbox case="UPPER" key="{addressMeta:getZipCode($address)}" width="76px" max="30" tab="{addressMeta:getCountry($address)},{addressMeta:getState($address)}" field="String"/>
						</widget>
					</row>
					<row>
					<text style="Prompt"><xsl:value-of select="resource:getString($constants,'country')"/>:</text>
					<widget colspan="3">
						<dropdown key="{addressMeta:getCountry($address)}" width="198px" tab="{envMeta:getSamplingLocation($env)},{addressMeta:getZipCode($address)}" field="String"/>
					</widget>
					</row>
					</TablePanel>
					</VerticalPanel>
					<!--button panel code-->
					<AbsolutePanel spacing="0" style="BottomButtonPanelContainer" align="center">
						<HorizontalPanel>
							<xsl:call-template name="commitButton">
								<xsl:with-param name="language">
									<xsl:value-of select="language" />
								</xsl:with-param>
							</xsl:call-template>
						</HorizontalPanel>
					</AbsolutePanel>
					<!--end button panel-->
				</VerticalPanel>
		</screen>
	</xsl:template>
</xsl:stylesheet>
